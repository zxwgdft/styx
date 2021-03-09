package com.styx.data.core;

import com.styx.data.core.terminal.Terminal;
import com.paladin.framework.spring.SpringBeanHelper;
import com.paladin.framework.spring.SpringContainer;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
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
public class CommandActionManager implements SpringContainer {

    private Map<Integer, CommandAction> commandActionMap = new HashMap<>();

    public boolean initialize() {
        // Spring 方式读取所有注入的CommandAction
        Map<String, CommandAction> classMap = SpringBeanHelper.getBeansByType(CommandAction.class);

        Map<Integer, CommandAction> commandActionMap = new HashMap<>();

        for (Map.Entry<String, CommandAction> entry : classMap.entrySet()) {
            CommandAction action = entry.getValue();
            commandActionMap.put(action.getCommand(), action);
        }

        this.commandActionMap = Collections.unmodifiableMap(commandActionMap);
        return true;
    }


    public byte[] doAction(ChannelHandlerContext context, Terminal terminal, Datagram datagram) {
        if (terminal.isMaintaining()) {
            terminal.setData(Constants.TERMINAL_WORK_STATUS_MAINTAIN, null, false);
            return null;
        }

        int command = datagram.getCommand();
        CommandAction action = commandActionMap.get(command);
        if (action != null) {
            return action.doAction(context, terminal, datagram);
        } else {
            log.error("接收到未存在的命令:" + command);
            context.close();
        }
        return null;
    }

}
