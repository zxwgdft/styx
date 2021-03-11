package com.paladin.monitor.service.config.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "终端设备视频地址")
public class DeviceLiveAddress {
    @ApiModelProperty("终端ID")
    private Integer id;
    @ApiModelProperty("视频地址1")
    private String liveUrl1;
    @ApiModelProperty("视频地址2")
    private String liveUrl2;
}
