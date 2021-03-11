package com.paladin.monitor.service.variable.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "终端模组配置")
public class ModuleConfigDTO {

    @ApiModelProperty("终端号")
    @NotNull(message = "终端号不能为空")
    private Integer terminalId;

    @ApiModelProperty("模组配置")
    private List<ModuleConfigItem> configs;

}