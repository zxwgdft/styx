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
public class TerminalAlarmHistory {

    @Id
    private Long id;
    private Integer terminalId;
    private Integer alarmId;
    private Date startTime;
    private Date endTime;
    private Boolean closed;
    private Boolean uploaded;


}
