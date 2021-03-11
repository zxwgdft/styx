package com.paladin.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/12/15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "统计市日报数据")
public class AnalysisHour4City {

    @ApiModelProperty("区县编码")
    private Integer districtCode;
    @ApiModelProperty("区县名称")
    private String districtName;
    @ApiModelProperty("每小时数据")
    private Map<Integer,DataHour> data;
}
