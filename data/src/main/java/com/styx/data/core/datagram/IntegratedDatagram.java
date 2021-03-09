package com.styx.data.core.datagram;

import com.styx.data.core.Datagram;
import com.styx.data.core.ProtocolException;
import com.styx.data.core.ProtocolUtil;

/**
 * 一体机数据报
 *
 * @author TontoZhou
 * @since 2020/10/23
 */
public class IntegratedDatagram extends Datagram {

    // 数据部分
    protected byte[] data;

    public IntegratedDatagram(byte[] bytes) {
        super(bytes);
        byte[] data = new byte[4];
        System.arraycopy(bytes, 26, data, 0, 4);
        byte checkCode = ProtocolUtil.calcCheckCode(bytes, 0, bytes.length - 1);

        if (checkCode != bytes[30]) {
            // 校验码不正确
            throw new ProtocolException("终端[" + terminalID + "]发送的数据校验码错误");
        }
    }
    
}
