package com.paladin.monitor.service.org.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author cxt
 * @date 2020/11/9
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgPermissionDTO {
    private String id;

    @ApiModelProperty("权限名称")
    private String name;

    @ApiModelProperty("权限对应URL")
    private String url;

    @ApiModelProperty("权限code")
    private String code;

    @ApiModelProperty("是否菜单")
    private Boolean isMenu;

    @ApiModelProperty("菜单图标")
    private String menuIcon;

    @ApiModelProperty("权限描述")
    private String description;

    @ApiModelProperty("父权限ID")
    private String parentId;

    @ApiModelProperty("列表顺序")
    private Integer listOrder;

    // 正常情况系统管理员应该不拥有业务相关功能权限，该部分权限应该由应用管理员通过授权赋权
    @ApiModelProperty("是否系统管理员权限")
    private Boolean isAdmin;

    @ApiModelProperty("是否可授权")
    private Boolean grantable;
}
