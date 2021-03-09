package com.styx.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
@Getter
@Setter
public class TerminalAlarm {

    @Id
    private Integer terminalId;
    @Id
    private Integer alarmId;
    private Date startTime;

}
