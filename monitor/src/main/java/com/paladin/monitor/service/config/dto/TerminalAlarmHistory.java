package com.paladin.monitor.service.config.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
@Getter
@Setter
public class TerminalAlarmHistory {

    private Integer terminalId;
    private Integer alarmId;
    private Date startTime;
    private Date endTime;
    private Boolean closed;

}
