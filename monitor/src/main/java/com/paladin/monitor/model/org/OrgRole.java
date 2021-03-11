package com.paladin.monitor.model.org;

import com.baomidou.mybatisplus.annotation.TableId;
import com.styx.common.api.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "角色")
public class OrgRole extends BaseModel {

    public static final String COLUMN_FIELD_ROLE_LEVEL = "roleLevel";

    public static final String COLUMN_FIELD_ENABLE = "enable";

    @TableId
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