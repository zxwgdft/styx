package com.styx.data.core.datagram;

import com.styx.data.core.Datagram;

/**
 * 分体机数据报
 *
 * @author TontoZhou
 * @since 2020/10/23
 */
public class SplitDatagram extends Datagram {
    public SplitDatagram(byte[] data) {
        super(data);
    }

    // 该数据报针对PLCSensorDataAction，command与其对应
    public int getCommand() {
        return 0xAA;
    }
}
