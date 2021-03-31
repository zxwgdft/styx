package com.styx.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/1/7
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "用于大屏展示报警数据")
public class DataAlarm4Show {
    @ApiModelProperty("站点名称")
    private String stationName;
    @ApiModelProperty("报警名称")
    private String alarmName;
    @ApiModelProperty("开始时间")
    private Long startTime;
}
