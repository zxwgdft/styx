package com.styx.data.core.terminal;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/12/31
 */
public interface TerminalListener {

    default void terminalOnline(Terminal terminal) {
        // do nothing
    }

    default void terminalOffline(Terminal terminal) {
        // do nothing
    }

    default void dataChangedHandle(Terminal terminal) {
        // do nothing
    }

    // 报警触发处理
    default void alarmTriggerHandle(Terminal terminal, List<AlarmStatus> alarmStatuses) {

    }

    // 报警管理处理
    default void alarmClosedHandle(Terminal terminal, List<AlarmStatus> alarmStatuses) {

    }


}
