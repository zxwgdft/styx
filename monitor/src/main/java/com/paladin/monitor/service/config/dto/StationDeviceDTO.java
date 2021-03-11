package com.paladin.monitor.service.config.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@ApiModel(description = "终端信息")
public class StationDeviceDTO {
    private Integer id;

    @ApiModelProperty("站点id")
    private Integer stationId;

    @ApiModelProperty("终端名称")
    private String name;

    @ApiModelProperty("终端UID")
    private String uid;

    @ApiModelProperty("终端类型")
    private Integer type;

    @ApiModelProperty("使用状态")
    private Boolean enabled;

    @ApiModelProperty("参数ids")
    private String variableIds;

    @ApiModelProperty("报警ids")
    private String alarmIds;
}
