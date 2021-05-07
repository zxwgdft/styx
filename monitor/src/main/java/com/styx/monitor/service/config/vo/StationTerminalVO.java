package com.styx.monitor.service.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/5/7
 */

@Getter
@Setter
@ApiModel(description = "站点-终端信息")
public class StationTerminalVO {

    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("终端名称")
    private String name;
    @ApiModelProperty("终端类型")
    private int type;

    @ApiModelProperty("站点ID")
    private int stationId;
    @ApiModelProperty("站点名称")
    private String stationName;
}
