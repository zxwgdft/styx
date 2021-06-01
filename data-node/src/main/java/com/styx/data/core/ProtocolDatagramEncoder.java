package com.styx.data.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 出站处理器
 */
@Slf4j
public class ProtocolDatagramEncoder extends MessageToByteEncoder<Datagram> {

    public ProtocolDatagramEncoder() {
        super(true);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg instanceof Datagram;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Datagram datagram, ByteBuf out) throws Exception {

        byte[] head = datagram.getHead();
        byte[] data = datagram.getData();

        int size = head.length + (data == null ? 0 : data.length) - 2;

        head[0] = (byte) (size >> 8 & 0xff);
        head[1] = (byte) (size & 0xff);

        byte check = 0;
        for (byte b : head) check ^= b;

        if (data != null)
            for (byte b : data) check ^= b;

        out.writeBytes(head);
        if (data != null)
            out.writeBytes(data);
        out.writeByte(check);
    }
}
