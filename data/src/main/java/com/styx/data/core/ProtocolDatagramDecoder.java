package com.styx.data.core;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于{@code LengthFieldBasedFrameDecoder}的解码类，对数据帧进一步解析出头部信息和数据内容
 */
@Slf4j
public class ProtocolDatagramDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolDatagramDecoder() {
        // 为防止攻击，可设置更小最大帧长度
        super(0xffff, 0, 2);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf buf = (ByteBuf) super.decode(ctx, in);

        if (buf == null) return null;

        int length = buf.readableBytes();
        if (length < 24) {
            throw new ProtocolException("协议错误，数据帧长度不足", null);
        }

        // 帧头部分
        byte[] head = new byte[23];
        buf.readBytes(head);

        // 12字节终端UID
        String uid = ByteBufUtil.hexDump(head, 2, 12);
        // 获取流水号（时间戳 + 2字节递增循环ID）
        long serialNumber = ProtocolUtil.getLong(head, 14);
        // 功能字，1字节
        int command = head[22] & 0xff;

        // 数据部分
        byte[] data = new byte[length - 24];
        buf.readBytes(data);

        byte toCheck = buf.readByte();
        // 校验
        byte check = 0;
        for (byte b : head) {
            check ^= b;
        }
        for (byte b : data) {
            check ^= b;
        }

        if (check != toCheck) {
            throw new ProtocolException("协议错误，校验异常", null);
        }

        return new Datagram(uid, command, serialNumber, head, data);
    }

}
