package com.paladin.monitor.service.data.vo;

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
@ApiModel(description = "大屏展示的每日数据")
@AllArgsConstructor
@NoArgsConstructor
public class DataDay4Show {
    @ApiModelProperty("变量ID")
    private Integer varId;
    @ApiModelProperty("日期天")
    private Integer day;
    @ApiModelProperty("峰值")
    private Float maxValue;
    @ApiModelProperty("谷值")
    private Float minValue;
}
