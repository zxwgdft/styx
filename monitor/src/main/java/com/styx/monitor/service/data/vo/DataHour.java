package com.styx.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2020/12/15
 */
@Getter
@Setter
@ApiModel(description = "每小时数据")
@AllArgsConstructor
@NoArgsConstructor
public class DataHour {
    @ApiModelProperty("多余字段")
    private Integer code;
    @ApiModelProperty("小时")
    private Integer hour;
    @ApiModelProperty("峰值")
    private Float maxValue;
    @ApiModelProperty("谷值")
    private Float minValue;
    @ApiModelProperty("平均值")
    private Float avgValue;
}
