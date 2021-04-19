package com.styx.data.core;

import java.time.LocalDateTime;

/**
 * @author TontoZhou
 * @since 2020/10/23
 */
public class ProtocolUtil {

    /**
     * 计算校验码(XOR)
     */
    public static byte calcXORCode(byte[] data) {
        return calcXORCode(data, 0, data.length);
    }

    /**
     * 计算校验码(XOR)
     */
    public static byte calcXORCode(byte[] data, int startIndex, int length) {
        byte bt = 0;
        int last = startIndex + length;
        for (int i = startIndex; i < last; i++) {
            bt ^= data[i];
        }
        return bt;
    }

    /**
     * 按照BCD码形式转换当前时间到字节
     */
    public static void setTimeByBCD(byte[] data, int startIndex) {
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


    //

    /**
     * 根据BCD码形式获取时间
     * <p>
     * 例如[0x20 0x16 0x10 0x31 0x15 0x04 0x23] 代表时间2016-10-31 15:04:23
     *
     * @param bytes 字节数组
     * @param index 开始位置
     * @return 时间的LocalDateTime对象
     */
    public static LocalDateTime getTimeByBCD(byte[] bytes, int index) {
        byte b1 = bytes[index++];
        byte b2 = bytes[index++];

        int year = ((b1 >> 4 & 0xff) * 1000) + ((b1 & 0x0f) * 100)
                + ((b2 >> 4 & 0xff) * 10) + (b2 & 0x0f);

        b1 = bytes[index++];
        b2 = bytes[index++];
        int month = ((b1 >> 4 & 0xff) * 1000) + ((b1 & 0x0f) * 100)
                + ((b2 >> 4 & 0xff) * 10) + (b2 & 0x0f);

        b1 = bytes[index++];
        int day = ((b1 >> 4 & 0xff) * 10) + (b1 & 0x0f);

        b1 = bytes[index++];
        int hour = ((b1 >> 4 & 0xff) * 10) + (b1 & 0x0f);

        b1 = bytes[index++];
        int minute = ((b1 >> 4 & 0xff) * 10) + (b1 & 0x0f);

        b1 = bytes[index];
        int second = ((b1 >> 4 & 0xff) * 10) + (b1 & 0x0f);

        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    /**
     * 获取8字节整数
     *
     * @param bytes 字节数组
     * @param index 开始位置
     */
    public static long getLong(byte[] bytes, int index) {
        return ((long) bytes[index] & 0xff) << 56 |
                ((long) bytes[index + 1] & 0xff) << 48 |
                ((long) bytes[index + 2] & 0xff) << 40 |
                ((long) bytes[index + 3] & 0xff) << 32 |
                ((long) bytes[index + 4] & 0xff) << 24 |
                ((long) bytes[index + 5] & 0xff) << 16 |
                ((long) bytes[index + 6] & 0xff) << 8 |
                (long) bytes[index + 7] & 0xff;
    }


    /**
     * 获取4字节整数
     *
     * @param bytes 字节数组
     * @param index 开始位置
     * @return
     */
    public static int getInteger(byte[] bytes, int index) {
        return (bytes[index] & 0xff) << 24 |
                (bytes[index + 1] & 0xff) << 16 |
                (bytes[index + 2] & 0xff) << 8 |
                bytes[index + 3] & 0xff;
    }

    /**
     * 读取量2字节整数
     *
     * @param bytes 字节数组
     * @param index 开始位置
     */
    public static int getSmallInteger(byte[] bytes, int index) {
        return (bytes[index] & 0xff) << 8 |
                bytes[index + 1] & 0xff;
    }

}
