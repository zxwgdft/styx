package com.paladin.monitor.model.org;

import com.baomidou.mybatisplus.annotation.TableId;
import com.styx.common.api.BaseModel;
import com.styx.common.api.DeletedBaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "权限")
public class OrgPermission extends BaseModel {

    public final static int TYPE_MENU = 1;
    public final static int TYPE_FUNCTION = 2;
    public final static int TYPE_DATA = 3;

    @TableId
    private String id;

    @ApiModelProperty("权限名称")
    private String name;

    @ApiModelProperty("权限对应URL")
    private String url;

    @ApiModelProperty("权限code")
    private String code;

    @ApiModelProperty("权限类型")
    private Integer type;

    @ApiModelProperty("菜单图标")
    private String menuIcon;

    @ApiModelProperty("权限描述")
    private String description;

    @ApiModelProperty("父权限ID")
    private String parentId;

    @ApiModelProperty("列表顺序")
    private Integer orderNo;

    // 正常情况系统管理员应该不拥有业务相关功能权限，该部分权限应该由应用管理员通过授权赋权
    @ApiModelProperty("是否系统管理员权限")
    private Boolean isAdmin;

    @ApiModelProperty("是否可授权")
    private Boolean grantable;


}