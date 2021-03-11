package com.paladin.monitor.web.internal.dto;

import com.paladin.monitor.core.config.CAlarm;
import com.paladin.monitor.core.config.COthers;
import com.paladin.monitor.core.config.CTerminal;
import com.paladin.monitor.core.config.CVariable;
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

    private long othersVersion;
    private COthers others;

    private String parentNodeCode;

}
