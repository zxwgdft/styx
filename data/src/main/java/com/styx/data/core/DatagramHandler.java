package com.styx.data.core;

import com.styx.common.spring.SpringBeanHelper;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.DefaultTerminalManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
    private DefaultTerminalManager terminalManager;

    private Map<Integer, CommandAction> commandActionMap = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Datagram datagram = (Datagram) msg;
        String uid = datagram.getUid();
        Terminal terminal = terminalManager.getTerminal(uid);
        if (terminal != null) {
            int command = datagram.getCommand();
            CommandAction action = commandActionMap.get(command);
            if (action != null) {
                byte[] responseData = action.doAction(terminal, datagram);
                datagram.setData(responseData);
                ctx.write(datagram);
            } else {
                throw new ProtocolException("协议错误，命令[" + command + "]不存在");
            }
        } else {
            // 终端不存在
            // TODO
        }
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
