package com.paladin.monitor.service.variable.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@ApiModel
public class ModuleConfigVO {

	@ApiModelProperty("终端号")
	private Integer terminalId;

	@ApiModelProperty("模组号")
	private Integer moduleNo;

	@ApiModelProperty("配置")
	private String config;


}