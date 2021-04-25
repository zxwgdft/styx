package com.styx.data.core.actions;

import com.styx.data.core.CommandAction;
import com.styx.data.core.Datagram;
import com.styx.data.core.ProtocolConstants;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.Variable;
import lombok.extern.slf4j.Slf4j;
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
public class DataCollectAction implements CommandAction {

    private static final int COMMAND = 0x0A;

    @Override
    public int getCommand() {
        return COMMAND;
    }

    @Override
    public byte[] doAction(Terminal terminal, Datagram datagram) {
        Collection<Variable> variables = terminal.getVariables();
        Map<Integer, Float> variableValueMap = new HashMap<>();

        byte[] data = datagram.getData();
        // 读取变量值
        for (Variable variable : variables) {
            int valueType = variable.getType();
            // 需要减去帧头1字节
            int bytePosition = variable.getBytePosition();
            int switchAddress = variable.getBitPosition();

            float value;

            try {
                switch (valueType) {
                    case ProtocolConstants.VALUE_TYPE_FLOAT:
                        value = getFloat(data, bytePosition);
                        break;
                    case ProtocolConstants.VALUE_TYPE_SWITCH:
                    case ProtocolConstants.VALUE_TYPE_FAULT:
                        value = getBit(data, bytePosition, switchAddress);
                        break;
                    case ProtocolConstants.VALUE_TYPE_INT:
                        value = getInt(data, bytePosition);
                        break;
                    default:
                        log.warn("变量(ID={})的数值类型{}不存在", variable.getId(), valueType);
                        continue;
                }
            } catch (Exception e) {
                log.warn("解析变量(ID={})获取数值异常", variable.getId());
                continue;
            }

            variableValueMap.put(variable.getId(), value);
        }

        terminal.setData(variableValueMap);

        return null;
    }

    private float getFloat(byte[] data, int address) {
        int value = ((data[address] & 0xFF) << 24)
                | ((data[address + 1] & 0xFF) << 16)
                | ((data[address + 2] & 0xFF) << 8)
                | (data[address + 3] & 0xFF);
        return Float.intBitsToFloat(value);
    }

    private int getBit(byte[] data, int address, int switchAddress) {
        return data[address] >> switchAddress & 0x01;
    }

    private int getInt(byte[] data, int address) {
        return ((data[address] & 0xFF) << 8)
                | (data[address + 1] & 0xFF);
    }
}
