package com.styx.data.core;


import com.styx.data.core.datagram.SplitDatagram;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息处理器
 *
 * @author shenq
 * @date 2018-04-03 13:52
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ProtocolInboundHandler extends ChannelInboundHandlerAdapter {


    @Autowired
    private CommandActionManager commandActionManager;

    @Autowired
    private TerminalManager terminalManager;


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("终端[" + ctx.channel().remoteAddress() + "]创建连接");
        Attribute<String> terminalIDAttr = ctx.channel().attr(Constants.TERMINAL_ID_KEY);
        terminalIDAttr.set(null);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            channelRead(ctx, (ByteBuf) msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void channelRead(ChannelHandlerContext ctx, ByteBuf buf) {
        if (!buf.hasArray()) {
            // 获取可读的字节数
            int length = buf.readableBytes();
            // 分配一个新的数组来保存字节
            byte[] array = new byte[length];
            // 字节复制到数组
            buf.getBytes(buf.readerIndex(), array);

            if (array[0] == (byte) 0xfa) {
                // 按照现在的解码器规则会出现第一帧多一个FA的情况，需要判断并删除
                byte[] na = new byte[length - 1];
                System.arraycopy(array, 1, na, 0, length - 1);
                array = na;
            }

            Datagram datagram = new SplitDatagram(array);


            if (datagram != null) {
                String terminalID = datagram.getTerminalID();
                Terminal terminal = terminalManager.getTerminal(terminalID);
                if (terminal != null) {
                    byte[] response = commandActionManager.doAction(ctx, terminal, datagram);
                    if (response != null) {
                        ByteBuf outBuf = Unpooled.buffer();
                        outBuf.writeBytes(response);
                        ctx.writeAndFlush(outBuf);
                    }
                } else {
                    log.warn("找不到终端[" + terminalID + "]");
                }
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            offline(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("终端[" + ctx.channel().remoteAddress() + "]断开连接");
        offline(ctx);
    }

    private void offline(ChannelHandlerContext ctx) {
        Attribute<String> terminalIDAttr = ctx.channel().attr(Constants.TERMINAL_ID_KEY);
        String terminalId = terminalIDAttr.get();
        if (terminalId != null && terminalId.length() > 0) {
            Terminal terminal = terminalManager.getTerminal(terminalId);
            if (terminal != null) {
                terminal.offline();
            }
        }
        ctx.close();
    }
}
