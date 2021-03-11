package com.paladin.monitor.service.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/1/12
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "站点下终端信息")
public class Station2Device {
    @ApiModelProperty("终端ID")
    private Integer id;
    @ApiModelProperty("终端名称")
    private String name;
    @ApiModelProperty("站点ID")
    private Integer stationId;
    @ApiModelProperty("站点名称")
    private String stationName;
}
