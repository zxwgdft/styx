package com.paladin.monitor.service.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2020/11/19
 */
@Getter
@Setter
@ApiModel("报警状况")
public class AlarmStatus {
    @ApiModelProperty("报警ID")
    private int id;
    @ApiModelProperty("第一次触发时间")
    private long startTime;
    @ApiModelProperty("处理时间")
    private long handleTime;
    @ApiModelProperty("报警名称")
    private String name;

}