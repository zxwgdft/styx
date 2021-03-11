package com.paladin.monitor.service.org.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/2/23
 */
@Getter
@Setter
public class GrantPermissionVO {

    private String id;

    @ApiModelProperty("权限名称")
    private String name;

    @ApiModelProperty("是否菜单")
    private Boolean isMenu;

    @ApiModelProperty("父权限ID")
    private String parentId;

}
