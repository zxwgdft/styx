package com.paladin.monitor.service.org.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "角色")
public class OrgRoleVO {

    private String id;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("角色等级")
    private Integer roleLevel;

    @ApiModelProperty("角色说明")
    private String roleDesc;

    @ApiModelProperty("是否启用")
    private Boolean enable;


}