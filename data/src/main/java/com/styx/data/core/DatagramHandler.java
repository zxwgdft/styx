package com.styx.data.core;

import com.styx.common.spring.SpringBeanHelper;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/10/23
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class DatagramHandler extends ChannelInboundHandlerAdapter implements ApplicationRunner {

    @Autowired
    private TerminalManager terminalManager;

    private Map<Integer, CommandAction> commandActionMap = new HashMap<>();


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("终端[{}]创建连接", ctx.channel().remoteAddress());
        }
        ctx.fireChannelActive();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Datagram) {
            Datagram datagram = (Datagram) msg;
            String uid = datagram.getUid();
            Terminal terminal = terminalManager.getTerminal(uid);
            if (terminal != null) {
                int command = datagram.getCommand();
                CommandAction action = commandActionMap.get(command);
                if (action != null) {
                    try {
                        byte[] responseData = action.doAction(terminal, datagram);
                        datagram.setData(responseData);
                        ctx.write(datagram);
                    } catch (Exception e) {
                        throw new ProtocolException("执行命令请求异常", datagram, e);
                    }
                } else {
                    throw new ProtocolException("协议错误，命令[" + command + "]不存在", datagram);
                }
            } else {
                throw new ProtocolException("协议错误，终端[" + uid + "]不存在", datagram);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);

        if (cause instanceof ProtocolException) {
            ProtocolException exception = (ProtocolException) cause;
            // TODO 记录详细异常
        }

        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("用户事件触发:{}", evt);
        }

        if (evt instanceof IdleStateEvent) {
            ctx.close();
        }

        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("终端[{}]断开连接", ctx.channel().remoteAddress());
        }
        ctx.fireChannelInactive();
    }


    @Override
    public void run(ApplicationArguments args) {
        // Spring 方式读取所有注入的CommandAction
        Map<String, CommandAction> classMap = SpringBeanHelper.getBeansByType(CommandAction.class);
        Map<Integer, CommandAction> commandActionMap = new HashMap<>();
        for (Map.Entry<String, CommandAction> entry : classMap.entrySet()) {
            CommandAction action = entry.getValue();
            commandActionMap.put(action.getCommand(), action);
        }
        this.commandActionMap = Collections.unmodifiableMap(commandActionMap);
        log.info("加载所有CommandAction完成");
    }
}
