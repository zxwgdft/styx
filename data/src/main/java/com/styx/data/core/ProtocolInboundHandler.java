package com.styx.data.core;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 数据报解析处理
 * <p>
 *
 * @author shenq
 * @date 2018-04-03 13:52
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ProtocolInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        int length = buf.readableBytes();
        if (length < 24) {
            throw new ProtocolException("协议错误，数据帧长度不足");
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
        for (byte b : head) check ^= b;
        for (byte b : data) check ^= b;
        if (check != toCheck) {
            throw new ProtocolException("协议错误，校验异常");
        }

        ctx.fireChannelRead(new Datagram(uid, command, serialNumber, data));
    }

}
