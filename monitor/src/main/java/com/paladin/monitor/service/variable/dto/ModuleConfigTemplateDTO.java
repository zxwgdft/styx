package com.paladin.monitor.service.variable.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author: cxt
 * @time: 2021/2/2
 */
@Getter
@Setter
@ToString
@ApiModel(description = "组态模板管理")
public class ModuleConfigTemplateDTO {
    @ApiModelProperty("终端号")
    private Integer id;

    @ApiModelProperty("模板名称")
    @NotBlank(message = "模板名称不能为空")
    private String name;

    @ApiModelProperty("模组号")
    @NotNull(message = "终端号不能为空")
    private Integer moduleNo;

    @ApiModelProperty("配置")
    @NotEmpty(message = "配置不能为空")
    private Integer[][] config;
}
