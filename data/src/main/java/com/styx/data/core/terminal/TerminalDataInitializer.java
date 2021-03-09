package com.styx.data.core.terminal;

import com.styx.data.model.TerminalInfo;

import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/11/17
 */
public interface TerminalDataInitializer {

    /**
     * 获取正在触发报警的ID集合
     *
     * @param id 终端ID
     * @return 触发报警中的报警ID集合
     */
    Map<Integer, Terminal.AlarmStatus> getAlarmTriggering(int id);

    /**
     * 获取终端信息
     *
     * @param id 终端ID
     * @return 终端信息
     */
    TerminalInfo getTerminalInfo(int id);

}
