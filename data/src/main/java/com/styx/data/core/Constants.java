package com.styx.data.core;

import io.netty.util.AttributeKey;

/**
 * @author TontoZhou
 * @since 2020/10/23
 */
public class Constants {

    public static final AttributeKey<String> TERMINAL_ID_KEY = AttributeKey.valueOf("TERMINAL_ID");


    public final static byte FRAME_HEAD = (byte) 0xFA;
    public final static byte FRAME_FOOT = (byte) 0xFB;


    public final static int VALUE_TYPE_FLOAT = 2;
    public final static int VALUE_TYPE_SWITCH = 0;
    public final static int VALUE_TYPE_FAULT = 3;


    // 工作状态：待机
    public final static int TERMINAL_WORK_STATUS_WAIT = 0;
    // 工作状态：处理中
    public final static int TERMINAL_WORK_STATUS_PROCESS = 1;
    // 工作状态：排放中
    public final static int TERMINAL_WORK_STATUS_DISCHARGE = 2;
    // 工作状态：未知
    public final static int TERMINAL_WORK_STATUS_UNKNOWN = 3;
    // 工作状态：维护中
    public final static int TERMINAL_WORK_STATUS_MAINTAIN = 4;
}
