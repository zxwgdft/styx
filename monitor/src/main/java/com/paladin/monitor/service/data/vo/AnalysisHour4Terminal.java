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
@ApiModel(description = "统计终端日报数据")
public class AnalysisHour4Terminal {

    @ApiModelProperty("变量ID")
    private Integer variableId;
    @ApiModelProperty("变量名称")
    private String variableName;

    @ApiModelProperty("每小时数据")
    private Map<Integer, DataHour> data;
}
