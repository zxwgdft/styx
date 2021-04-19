package com.styx.data.core.actions;

import com.styx.data.core.CommandAction;
import com.styx.data.core.Datagram;
import com.styx.data.core.terminal.Terminal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 时间校验请求处理，直接返回当前服务器时间即可
 */
@Component
@Slf4j
public class TimeCheckAction implements CommandAction {

    private static final int COMMAND = 0x0C;

    @Override
    public int getCommand() {
        return COMMAND;
    }

    @Override
    public byte[] doAction(Terminal terminal, Datagram datagram) {
        return null;
    }


}
