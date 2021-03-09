package com.styx.data.core.actions;

import com.styx.data.core.CommandAction;
import com.styx.data.core.Constants;
import com.styx.data.core.Datagram;
import com.styx.data.core.ProtocolUtil;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.Variable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/10/27
 */
@Slf4j
@Component
public class PLCSensorDataAction implements CommandAction {

    private static final int COMMAND = 0xAA;
    private static final int REAL_COMMAND = 0x0A;

    @Value("${data.protocol.variable.id-ppf}")
    private int variable_id_ppf;

    @Value("${data.protocol.variable.id-ysdyw}")
    private int variable_id_ysdyw;

    @Override
    public int getCommand() {
        return COMMAND;
    }

    @Override
    public byte[] doAction(ChannelHandlerContext context, Terminal terminal, Datagram datagram) {
        Collection<Variable> variables = terminal.getVariables();

        Map<Integer, Float> variableValueMap = new HashMap<>();

        // 读取变量值
        for (Variable variable : variables) {
            int valueType = variable.getValueType();
            // 需要减去帧头1字节
            int address = variable.getAddressStart() - 1;

            if (address < 0) {
                log.warn("变量[id:" + variable.getId() + "]解析地址异常，不能为：" + address);
                continue;
            }

            int switchAddress = variable.getSwitchAddress();

            float value;
            if (valueType == Constants.VALUE_TYPE_FLOAT) {
                // 浮点型数据类型，4位
                value = getFloat(datagram, address);
            } else if (valueType == Constants.VALUE_TYPE_SWITCH || valueType == Constants.VALUE_TYPE_FAULT) {
                // 开关量解析
                value = getSwitch(datagram, address, switchAddress);
            } else {
                // 整型数据解析
                // 似乎老系统未启用，写法存疑
                value = getInt(datagram, address);
            }

            variableValueMap.put(variable.getId(), value);
        }

        Float PFFValue = variableValueMap.get(variable_id_ppf);
        Float YSDYWValue = variableValueMap.get(variable_id_ysdyw);

        int workStatus = Constants.TERMINAL_WORK_STATUS_UNKNOWN;

        if (PFFValue != null) {
            int pff = PFFValue.intValue();
            if (pff == 1) {
                workStatus = Constants.TERMINAL_WORK_STATUS_DISCHARGE;
            } else {
                if (YSDYWValue != null) {
                    int ysdy = YSDYWValue.intValue();
                    if (pff == 0 && ysdy == 1) {
                        workStatus = Constants.TERMINAL_WORK_STATUS_PROCESS;
                    } else if (ysdy == 0) {
                        workStatus = Constants.TERMINAL_WORK_STATUS_WAIT;
                    }
                }
            }
        }


        if (workStatus == Constants.TERMINAL_WORK_STATUS_PROCESS || workStatus == Constants.TERMINAL_WORK_STATUS_DISCHARGE) {
            terminal.setData(workStatus, variableValueMap, true);
        } else {
            terminal.setData(workStatus, variableValueMap, false);
        }

        Attribute<String> terminalIDAttr = context.channel().attr(Constants.TERMINAL_ID_KEY);
        terminalIDAttr.setIfAbsent(terminal.getUid());


        // 返回
        byte[] response = new byte[18];

        response[0] = Constants.FRAME_HEAD;
        response[1] = REAL_COMMAND;

        // 从请求数据帧中拷贝终端ID，12位
        datagram.copyTerminalID(response, 2);

        response[14] = 24;
        response[15] = 24;

        // 计算校验码
        response[16] = ProtocolUtil.calcCheckCode(response, 1, 15);
        response[17] = Constants.FRAME_FOOT;

        return response;
    }

    private float getFloat(Datagram datagram, int address) {
        byte[] data = datagram.getData();
        int value = ((data[address] & 0xFF) << 24)
                | ((data[address + 1] & 0xFF) << 16)
                | ((data[address + 2] & 0xFF) << 8)
                | (data[address + 3] & 0xFF);
        return Float.intBitsToFloat(value);
    }

    private int getSwitch(Datagram datagram, int address, int switchAddress) {
        byte data = datagram.getData()[address];
        return data >> switchAddress & 0x01;
    }

    private int getInt(Datagram datagram, int address) {
        byte[] data = datagram.getData();
        return ((data[address] & 0xFF) << 8)
                | (data[address + 1] & 0xFF);
    }
}
