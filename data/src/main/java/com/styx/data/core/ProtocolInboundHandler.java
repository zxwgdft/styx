package com.styx.data.core;


import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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

import java.time.LocalDateTime;

/**
 * 消息处理器
 * <p>
 * 因为本处理类属于业务上最终处理类，我们不必调用ChannelHandlerContext.fireXXXXX
 * 方法抛到下一个处理类，但因此缺失了默认最终处理类 TailContext 中的逻辑，所以
 * 在相关方法中补上释放内存的代码（ReferenceCountUtil.release）
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
        channelRead(ctx, (ByteBuf) msg);
        // 因为没有 ctx.fireChannelRead(msg)，所以这里我们需要手动调用释放内存
        ReferenceCountUtil.release(msg);
    }

    private void channelRead(ChannelHandlerContext ctx, ByteBuf buf) {
        if (!buf.hasArray()) {
            // 帧头部分
            byte[] head = new byte[23];
            buf.readBytes(head);

            if (head[0] != 0xfa) {
                throw new ProtocolException("协议错误，帧头异常");
            }

            // 12字节终端UID
            String uid = ByteBufUtil.hexDump(head, 1, 12);
            // 7位字节时间
            LocalDateTime time = ProtocolUtil.getTimeByBCD(head, 13);
            // 功能字，1字节
            int command = head[20] & 0xff;

            // 数据部分
            byte[] data = new byte[1000];
            buf.readBytes(data);

            byte toCheck = buf.readByte();
            // 校验
            byte check = 0;
            for (byte b : head) check ^= b;
            for (byte b : data) check ^= b;
            if (check != toCheck) {
                throw new ProtocolException("协议错误，校验异常");
            }

            Datagram datagram = new Datagram(uid, command, time, head, data);

            Terminal terminal = terminalManager.getTerminal(uid);
            if (terminal != null) {
                byte[] response = commandActionManager.doAction(ctx, terminal, datagram);
                if (response != null) {
                    ByteBuf outBuf = Unpooled.buffer();
                    outBuf.writeBytes(response);
                    ctx.writeAndFlush(outBuf);
                }
            } else {
                log.warn("找不到终端[" + uid + "]");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        // 因为没有 ctx.fireExceptionCaught(cause)，所以这里我们需要手动调用释放内存
        ReferenceCountUtil.release(cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            // 超时离线
            offline(ctx);
        }
        // 因为没有 ctx.fireUserEventTriggered(evt)，所以这里我们需要手动调用释放内存
        ReferenceCountUtil.release(evt);
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
