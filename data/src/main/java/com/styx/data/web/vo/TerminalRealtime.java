package com.styx.data.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/11/16
 */
@Getter
@Setter
@ApiModel(description = "终端实时数据")
public class TerminalRealtime {

    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("终端名称")
    private String name;
    @ApiModelProperty("站点ID")
    private int stationId;
    @ApiModelProperty("站点名称")
    private String stationName;
    @ApiModelProperty("是否在线")
    private boolean isOnline;
    @ApiModelProperty("最近登录时间")
    private long lastLoginTime;
    @ApiModelProperty("最近工作时间")
    private long lastWorkTime;
    @ApiModelProperty("工作累计时间(分钟)")
    private int workTotalTime;
    @ApiModelProperty("当前工作累计时间(分钟)")
    private int workCurrentTime;
    @ApiModelProperty("工作状态")
    private int workStatus;
    @ApiModelProperty("是否维护中")
    private boolean isMaintaining;
    @ApiModelProperty("最近一次接收数据时间")
    private long dataUpdateTime;
    @ApiModelProperty("变量数据")
    private Map<Integer, Float> variableValues;
    @ApiModelProperty("是否报警中")
    private boolean isAlarmTriggering;
}
