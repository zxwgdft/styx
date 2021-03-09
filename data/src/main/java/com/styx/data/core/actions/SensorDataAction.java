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
 * @since 2020/10/26
 */
@Slf4j
@Component
public class SensorDataAction implements CommandAction {

    private static final int COMMAND = 0x0A;

    @Value("${data.protocol.variable.id-yyw}")
    private int variable_id_yyw;

    @Override
    public int getCommand() {
        return COMMAND;
    }

    @Override
    public byte[] doAction(ChannelHandlerContext context, Terminal terminal, Datagram datagram) {

        int sensorType = datagram.getSensorAddr() & 0xff;
        int dataValue = getDataValue(datagram);

        Map<Integer, Float> valueMap = new HashMap<>();
        if (sensorType == 19 || sensorType == 20 || sensorType == 21 || sensorType == 22) {
            Collection<Variable> variables = terminal.getVariableBySensorType(sensorType);
            float value = dataValue / 100f;
            for (Variable variable : variables) {
                valueMap.put(variable.getId(), value);
            }
        } else if (sensorType == 23) {
            Collection<Variable> variables = terminal.getVariableBySensorType(sensorType);
            for (Variable variable : variables) {
                int switchAddress = variable.getSwitchAddress();
                int value = dataValue >> switchAddress & 0x01;
                valueMap.put(variable.getId(), (float) value);
            }
        }

        Float YYWValue = valueMap.get(variable_id_yyw);

        int workStatus = Constants.TERMINAL_WORK_STATUS_UNKNOWN;
        if (YYWValue != null) {
            int value = YYWValue.intValue();
            //判断该终端的运行状态
            if (value == 1) {
                //原水有液位   排放中
                workStatus = Constants.TERMINAL_WORK_STATUS_DISCHARGE;
            } else if (value == 0) {
                //原水无液位 设备停机
                workStatus = Constants.TERMINAL_WORK_STATUS_WAIT;
            }
        }

        terminal.setData(workStatus, valueMap, false);

        Attribute<String> terminalIDAttr = context.channel().attr(Constants.TERMINAL_ID_KEY);
        terminalIDAttr.setIfAbsent(terminal.getUid());

        // 返回
        byte[] response = new byte[18];

        response[0] = Constants.FRAME_HEAD;
        response[1] = COMMAND;

        // 从请求数据帧中拷贝终端ID，12位
        datagram.copyTerminalID(response, 2);

        response[14] = datagram.getSensorType();
        response[15] = datagram.getSensorAddr();

        // 计算校验码
        response[16] = ProtocolUtil.calcCheckCode(response, 1, 15);
        response[17] = Constants.FRAME_FOOT;

        return response;
    }

    private int getDataValue(Datagram datagram) {
        byte[] data = datagram.getData();
        return ((data[0] & 0xFF) << 24)
                | ((data[1] & 0xFF) << 16)
                | ((data[2] & 0xFF) << 8)
                | (data[3] & 0xFF);
    }
}
