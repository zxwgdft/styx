package com.paladin.monitor.service.config.vo;

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
@ApiModel(description = "终端设备简要信息")
public class StationDeviceSimpleVO {

    @ApiModelProperty("站点id")
    private Integer id;

    @ApiModelProperty("终端名称")
    private String name;

    @ApiModelProperty("终端UID")
    private String uid;

    @ApiModelProperty("终端类型")
    private Integer type;
}
