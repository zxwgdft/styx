package com.styx.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/11/16
 */
@Getter
@Setter
@ApiModel(description = "终端实时数据")
public class TerminalRealData {

    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("是否在线")
    private boolean isOnline;
    @ApiModelProperty("最近一次登录时间")
    private long lastLoginTime;
    @ApiModelProperty("最近一次工作时间")
    private long lastWorkTime;
    @ApiModelProperty("工作累计时间(分钟)")
    private int workTotalTime;
    @ApiModelProperty("变量数据")
    private Map<Integer, Float> variableValues;
    @ApiModelProperty("报警情况")
    private List<AlarmStatus> alarmStatuses;
}
