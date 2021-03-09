package com.styx.data.core;

import java.time.LocalDateTime;

/**
 * @author TontoZhou
 * @since 2020/10/23
 */
public class ProtocolUtil {

    /**
     * 计算校验码，字节求和
     */
    public static byte calcCheckCode(byte[] data) {
        return calcCheckCode(data, 0, data.length);
    }

    /**
     * 计算校验码，字节求和
     */
    public static byte calcCheckCode(byte[] data, int startIndex, int length) {
        byte bt = 0;
        int last = startIndex + length;
        for (int i = startIndex; i < last; i++) {
            bt += data[i];
        }
        return bt;
    }

    /**
     * 按照BCD码形式转换当前时间到字节
     */
    public static void setDateTime(byte[] data, int startIndex) {
        LocalDateTime dt = LocalDateTime.now();
        int year = dt.getYear();
        data[startIndex++] = intToByte(year / 100);
        data[startIndex++] = intToByte(year % 100);
        data[startIndex++] = intToByte(dt.getMonthValue());
        data[startIndex++] = intToByte(dt.getDayOfMonth());
        data[startIndex++] = intToByte(dt.getHour());
        data[startIndex++] = intToByte(dt.getMinute());
        data[startIndex] = intToByte(dt.getSecond());
    }

    // 20 -> 0x20
    private static byte intToByte(int n) {
        if (n < 10) {
            return (byte) n;
        }
        return (byte) (n / 10 * 16 + n % 10);
    }
}
