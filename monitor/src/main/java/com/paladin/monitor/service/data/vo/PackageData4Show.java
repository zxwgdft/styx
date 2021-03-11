package com.paladin.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/1/5
 */
@Getter
@Setter
@ApiModel(description = "用于大屏展示数据包")
public class PackageData4Show {
    @ApiModelProperty("设备终端总数")
    private int totalTerminalNum;
    @ApiModelProperty("设备终端在线数")
    private int onlineTerminalNum;
    @ApiModelProperty("设备终端报警数")
    private int alarmTerminalNum;
    @ApiModelProperty("总累计流量")
    private int totalFlow;

    @ApiModelProperty("站点流量排行")
    private List<DataFlow4Show> totalFlowRanking;
//    @ApiModelProperty("所有终端累计工作时长")
//    private List<DataWorkTime4Show> totalWorkTimeRanking;
    @ApiModelProperty("所有终端报警信息")
    private List<DataAlarm4Show> stationAlarms;
    @ApiModelProperty("各级医院数")
    private List<StationCount> stationCounts;
    @ApiModelProperty("所有终端状态数据")
    private List<DataTerminal4Show> terminals;
}
