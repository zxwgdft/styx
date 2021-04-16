package com.styx.data.core;

/**
 * @author TontoZhou
 * @since 2020/10/26
 */
public class ProtocolException extends RuntimeException {

    public ProtocolException() {
        super("协议异常");
    }
    
    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

}
