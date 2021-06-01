package com.styx.data.core.terminal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/4/28
 */
@Getter
@Setter
public class TerminalPersistedInfo {

    private int id;
    private long workTotalTime;
    private long dataUpdateTime;
    private long lastLoginTime;

}
