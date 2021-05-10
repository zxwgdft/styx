package com.styx.data.service;

import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author TontoZhou
 * @since 2021/5/10
 */
@Slf4j
@Service
public class TerminalEventService implements TerminalListener {

    public void terminalOnline(Terminal terminal) {
        // do nothing
    }

    public void terminalOffline(Terminal terminal) {
        // do nothing
    }

}
