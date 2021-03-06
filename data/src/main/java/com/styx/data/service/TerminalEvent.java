package com.styx.data.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;

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

    public TerminalEvent() {

    }

    public TerminalEvent(int id, int event, long time, String data) {
        this.id = id;
        this.event = event;
        this.time = time;
        this.data = data;
    }

    public TerminalEvent(int id, int event, String data) {
        this.id = id;
        this.event = event;
        this.time = System.currentTimeMillis();
        this.data = data;
    }

    public TerminalEvent(int id, int event) {
        this.id = id;
        this.event = event;
        this.time = System.currentTimeMillis();
    }
}
