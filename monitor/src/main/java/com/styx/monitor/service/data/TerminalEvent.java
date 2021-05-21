package com.styx.monitor.service.data;

import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/5/12
 */
@Getter
@Setter
public class TerminalEvent {

    private int id;
    private int event;
    private long time;
    private String data;

}
