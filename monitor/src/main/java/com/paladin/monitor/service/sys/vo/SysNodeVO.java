package com.paladin.monitor.service.sys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SysNodeVO {

    @ApiModelProperty("节点编码")
    private String code;

    @ApiModelProperty("节点名称")
    private String name;

    @ApiModelProperty("父节点ID")
    private String parentCode;

    @ApiModelProperty("是否默认节点")
    private Boolean isDefault;
}