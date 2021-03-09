package com.styx.data.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/1/5
 */
@Getter
@Setter
@ApiModel(description = "用于管理员分析数据包")
public class PackageData4Manager {
    
    @ApiModelProperty("报警设备ID集合")
    private List<Integer> alarmTerminalIds;
    @ApiModelProperty("在线设备ID集合")
    private List<Integer> onlineTerminalIds;

    @ApiModelProperty("所有终端累计流量")
    private Map<Integer, Float> totalFlowMap;
    @ApiModelProperty("所有终端累计工作时长")
    private Map<Integer, Long> totalWorkTimeMap;
    @ApiModelProperty("所有报警信息")
    private List<TerminalAlarms> terminalAlarmsList;
}
