package com.paladin.monitor.service.sys.dto;

import com.paladin.monitor.core.security.Menu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author TontoZhou
 * @since 2019/12/26
 */
@Getter
@Setter
@ApiModel(description = "登录成功返回信息")
public class LoginSuccess implements Serializable {

    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户类型")
    private int userType;
    @ApiModelProperty("认证token")
    private String token;
    @ApiModelProperty("是否系统管理员")
    private boolean isSystemAdmin;
    @ApiModelProperty("权限Code")
    private Collection<String> permissionCodes;
    @ApiModelProperty("用户菜单")
    private List<Menu> menus;
}
