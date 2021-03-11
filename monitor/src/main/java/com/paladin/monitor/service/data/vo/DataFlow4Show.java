package com.paladin.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/1/7
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "用于大屏展示流量数据")
public class DataFlow4Show {
    @ApiModelProperty("站点名称")
    private String stationName;
    @ApiModelProperty("流量")
    private Float flowValue;
}
