package com.styx.data.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TontoZhou
 * @since 2020/10/23
 */
@Getter
@AllArgsConstructor
public class Datagram {

    private String uid;
    private int command;
    private long serialNumber;
    private byte[] head;
    private byte[] data;

    public void setData(byte[] data) {
        this.data = data;
    }

}
