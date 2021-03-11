package com.paladin.monitor.service.sys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
@ApiModel
public class SysConstantVO {

    @ApiModelProperty("分类")
    private String type;

    @ApiModelProperty("code")
    private String code;

    @ApiModelProperty("name")
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("序号")
    private Integer orderNo;
}
