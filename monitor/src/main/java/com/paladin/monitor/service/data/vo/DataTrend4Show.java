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
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "大屏显示历史趋势数据")
public class DataTrend4Show {
    @ApiModelProperty("统计标题")
    private String title;
    @ApiModelProperty("每天数据")
    private DataDay4Show[] data;
}
