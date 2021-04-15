package com.styx.monitor.service.config.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/10/30
 */
@Getter
@Setter
public class VersionConfig {
    private long variableVersion;
    private List<CVariable> variables;

    private long alarmVersion;
    private List<CAlarm> alarms;

    private long terminalVersion;
    private List<CTerminal> terminals;

}
