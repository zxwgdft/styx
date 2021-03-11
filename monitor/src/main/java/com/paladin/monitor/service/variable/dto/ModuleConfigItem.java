package com.paladin.monitor.service.variable.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel(description = "每个模组详细配置")
public class ModuleConfigItem {

    @ApiModelProperty("模组号")
    @NotNull(message = "模组号不能为空")
    private Integer moduleNo;

    @ApiModelProperty("配置")
    @NotNull(message = "配置不能为空")
    private Integer[][] config;

}