package com.styx.data.service.dto;

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
    private long othersVersion;

    public VersionUpdate(String serverNode) {
        this.serverNode = serverNode;
    }

    public VersionUpdate() {
    }
}
