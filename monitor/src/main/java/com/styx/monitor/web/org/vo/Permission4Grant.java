package com.styx.monitor.web.org.vo;

import com.styx.monitor.model.org.OrgPermission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/10/19
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "角色权限列表")
public class Permission4Grant {

    @ApiModelProperty("权限列表")
    private List<OrgPermission> permissions;

    @ApiModelProperty("角色拥有的权限ID")
    private List<String> hasPermissions;

}
