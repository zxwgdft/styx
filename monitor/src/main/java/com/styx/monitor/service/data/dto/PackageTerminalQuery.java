package com.styx.monitor.service.data.dto;

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
@ApiModel(description = "终端数据分析查询")
public class PackageTerminalQuery {

    @ApiModelProperty("终端ID")
    private Integer terminalId;
    @ApiModelProperty(hidden = true)
    private List<Integer> variableIds;
    @ApiModelProperty(hidden = true)
    private Integer startDay;
    @ApiModelProperty(hidden = true)
    private Integer endDay;
    @ApiModelProperty("统计日期")
    private String date;

}
