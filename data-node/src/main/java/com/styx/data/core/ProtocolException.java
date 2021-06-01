package com.styx.data.core;

import io.netty.channel.Channel;

/**
 * @author TontoZhou
 * @since 2020/10/26
 */
public class ProtocolException extends RuntimeException {

    private Datagram datagram;

    public ProtocolException(String message, Datagram datagram) {
        super(message);
        this.datagram = datagram;
    }

    public ProtocolException(String message, Datagram datagram, Throwable cause) {
        super(message, cause);
        this.datagram = datagram;
    }

    public Datagram getDatagram() {
        return datagram;
    }
}
