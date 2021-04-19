package com.styx.data.core;

import io.netty.util.AttributeKey;

/**
 * @author TontoZhou
 * @since 2020/10/23
 */
public class Constants {

    public static final AttributeKey<String> TERMINAL_ID_KEY = AttributeKey.valueOf("TERMINAL_ID");

    public final static int VALUE_TYPE_INT = 1;
    public final static int VALUE_TYPE_FLOAT = 2;
    public final static int VALUE_TYPE_SWITCH = 0;
    public final static int VALUE_TYPE_FAULT = 3;


}
