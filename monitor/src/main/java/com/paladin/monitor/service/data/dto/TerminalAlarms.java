package com.paladin.monitor.service.data.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * @author TontoZhou
 * @since 2020/11/19
 */
@Getter
@Setter
public class TerminalAlarms {
    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("终端名称")
    private String name;
    @ApiModelProperty("站点ID")
    private int stationId;
    @ApiModelProperty("站点名称")
    private String stationName;
    @ApiModelProperty("报警")
    private Collection<AlarmStatus> alarms;
}
