package com.styx.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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

    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer terminalId;
    private Integer alarmId;
    private Date startTime;
    private Date endTime;
    private Boolean closed;
    private Boolean uploaded;


}
