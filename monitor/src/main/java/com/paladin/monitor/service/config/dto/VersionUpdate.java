package com.paladin.monitor.service.config.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2020/11/3
 */
@Getter
@Setter
public class VersionUpdate {

    private String serverNode;

    private long variableVersion;
    private long alarmVersion;
    private long terminalVersion;
}
