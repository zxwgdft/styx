package com.styx.data.core.terminal;

/**
 * @author TontoZhou
 * @since 2020/11/17
 */
public interface TerminalAlarmHandler {

    // 报警触发处理
    void alarmTriggerHandle(int terminalId, int alarmId, long startTime);

    // 报警管理处理
    void alarmClosedHandle(int terminalId, int alarmId, long startTime);

}
