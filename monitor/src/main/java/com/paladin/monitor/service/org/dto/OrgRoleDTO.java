package com.paladin.monitor.service.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel(description = "角色")
public class OrgRoleDTO {

    // id
    private String id;

    @ApiModelProperty("角色名称")
    @NotEmpty(message = "角色名称不能为空")
    private String roleName;

//    @ApiModelProperty("角色权限等级")
//    @NotNull(message = "角色权限等级不能为空")
//    private Integer roleLevel;

    @ApiModelProperty("角色说明")
    private String roleDesc;

    @ApiModelProperty("是否启用")
    @NotNull(message = "是否启用不能为空")
    private Boolean enable;


}