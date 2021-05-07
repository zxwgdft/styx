package com.styx.monitor.service.config.vo;

import com.styx.monitor.service.config.cache.SimpleAlarm;
import com.styx.monitor.service.config.cache.SimpleVariable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/5/7
 */
@Getter
@Setter
@ApiModel(description = "终端详细信息")
public class TerminalDetailVO {

    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("终端名称")
    private String name;
    @ApiModelProperty("终端类型")
    private int type;

    @ApiModelProperty("站点ID")
    private int stationId;
    @ApiModelProperty("站点名称")
    private String stationName;

    @ApiModelProperty(hidden = true)
    private String variableIds;
    @ApiModelProperty(hidden = true)
    private String alarmIds;

    @ApiModelProperty("变量列表")
    private List<SimpleVariable> variables;
    @ApiModelProperty("报警列表")
    private List<SimpleAlarm> alarms;

}
