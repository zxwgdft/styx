package com.paladin.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/12/15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "统计站点月报数据")
public class AnalysisDay4Station {

    @ApiModelProperty("站点名称")
    private String stationName;
    @ApiModelProperty("终端ID")
    private Integer terminalId;
    @ApiModelProperty("终端名称")
    private String terminalName;

    @ApiModelProperty("每小时数据")
    private Map<Integer, DataDay> data;
}
