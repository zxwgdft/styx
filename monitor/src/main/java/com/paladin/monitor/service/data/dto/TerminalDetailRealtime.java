package com.paladin.monitor.service.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2020/12/1
 */
@Getter
@Setter
@ApiModel(description = "终端设备详细信息")
public class TerminalDetailRealtime {

    @ApiModelProperty("站点id")
    private Integer id;

    @ApiModelProperty("站点id")
    private Integer stationId;

    @ApiModelProperty("终端名称")
    private String stationName;

    @ApiModelProperty("终端UID")
    private String uid;

    @ApiModelProperty("终端类型")
    private Integer type;

    @ApiModelProperty("终端数据")
    private TerminalRealtime data;

}
