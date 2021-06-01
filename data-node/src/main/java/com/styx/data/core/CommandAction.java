package com.styx.data.core;

import com.styx.data.core.terminal.Terminal;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author TontoZhou
 * @since 2020/10/23
 */
public interface CommandAction {

    int getCommand();

    byte[] doAction(Terminal terminal, Datagram datagram);

}
