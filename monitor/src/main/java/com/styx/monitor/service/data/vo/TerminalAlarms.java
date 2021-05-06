package com.styx.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * @author TontoZhou
 * @since 2020/11/19
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "终端报警信息")
public class TerminalAlarms {
    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("报警")
    private Collection<AlarmStatus> alarms;
}
