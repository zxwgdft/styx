package com.styx.data.core.terminal;

import java.util.List;

/**
 * 终端管理器
 *
 * @author TontoZhou
 * @since 2021/4/20
 */
public interface TerminalManager {

    /**
     * 根据UID获取终端（UID为全局唯一，针对系统内外通用）
     */
    Terminal getTerminal(String uid);

    /**
     * 根据ID获取终端（ID只针对系统内部使用）
     */
    Terminal getTerminal(int id);

    /**
     * 获取所有终端容器
     */
    TerminalContainer getTerminalContainer();

    /**
     * 获取所有变量容器
     */
    VariableContainer getVariableContainer();

    /**
     * 获取所有报警容器
     */
    AlarmContainer getAlarmContainer();

    /**
     * 调度分发终端数据变更事件
     */
    void dispatchTerminalDataChangeEvent(Terminal terminal);

    /**
     * 调度分发终端报警触发事件
     */
    void dispatchTerminalAlarmTriggerEvent(Terminal terminal, List<AlarmStatus> alarmStatuses);

    /**
     * 调度分发终端报警关闭事件
     */
    void dispatchTerminalClosedTriggerEvent(Terminal terminal, List<AlarmStatus> alarmStatuses);

}
