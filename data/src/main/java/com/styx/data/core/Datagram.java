package com.styx.data.core;

import io.netty.buffer.ByteBufUtil;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;

/**
 * @author TontoZhou
 * @since 2020/10/23
 */
@Getter
public class Datagram {

    protected byte[] data;

    protected String terminalID;
    protected int command;
    protected Date receiveTime;

    // 传感器类型
    private byte sensorType;
    // 传感器地址
    private byte sensorAddr;

    public Datagram(byte[] data) {

        this.data = data;

        // 功能字，1字节
        this.command = data[0] & 0xff;
        // 客户码， 4字节，用不上暂时注释
        // byte[] customerCode = new byte[4];
        // System.arraycopy(data, 1, customerCode, 0, 4);

        // 终端UID
        this.terminalID = ByteBufUtil.hexDump(data, 5, 12);

        // 7位字节时间
        this.receiveTime = getTime();

        this.sensorType = data[24];
        this.sensorAddr = data[25];

    }


    // 根据BCD码形式获取时间， 例如[0x20 0x16 0x10 0x31 0x15 0x04 0x23] 代表时间2016-10-31 15:04:23
    protected Date getTime() {

        int year = (data[17] >> 4 & 0xff) * 1000;
        year += (data[17] & 0x0f) * 100;
        year += (data[18] >> 4 & 0xff) * 10;
        year += data[18] & 0x0f;

        if (year == 0) {
            return new Date();
        }

        int month = (data[19] >> 4 & 0xff) * 10;
        month += data[19] & 0x0f;

        int day = (data[20] >> 4 & 0xff) * 10;
        day += data[20] & 0x0f;

        int hour = (data[21] >> 4 & 0xff) * 10;
        hour += data[21] & 0x0f;

        int minute = (data[22] >> 4 & 0xff) * 10;
        minute += data[22] & 0x0f;

        int second = (data[23] >> 4 & 0xff) * 10;
        second += data[23] & 0x0f;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }


    /**
     * 拷贝终端ID字节到目标byte[]
     *
     * @param target     目标byte[]
     * @param startIndex 目标byte[]开始拷贝序号
     */
    public void copyTerminalID(byte[] target, int startIndex) {
        System.arraycopy(data, 5, target, startIndex, 12);
    }

}
