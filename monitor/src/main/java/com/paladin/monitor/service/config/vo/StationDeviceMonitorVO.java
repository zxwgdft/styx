package com.paladin.monitor.service.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "终端设备信息")
public class StationDeviceMonitorVO {
    @ApiModelProperty("终端设备ID")
    private Integer id;

    @ApiModelProperty("站点名称")
    private String stationName;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("终端类型")
    private Integer type;

    @ApiModelProperty("终端UID")
    private String uid;

    @ApiModelProperty("是否第三方")
    private Boolean isThird;


}
