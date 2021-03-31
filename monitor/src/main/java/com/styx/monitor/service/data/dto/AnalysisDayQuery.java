package com.styx.monitor.service.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author TontoZhou
 * @since 2020/12/15
 */
@Getter
@Setter
@ApiModel(description = "月报统计查询条件")
public class AnalysisDayQuery {

    @ApiModelProperty("统计日期")
    private String date;
    @ApiModelProperty("统计终端ID")
    private Integer terminalId;
    @ApiModelProperty("统计站点ID")
    private Integer stationId;
    @ApiModelProperty("统计变量")
    private Integer variableId;
    @ApiModelProperty("统计地区")
    private Integer districtCode;
    

}
