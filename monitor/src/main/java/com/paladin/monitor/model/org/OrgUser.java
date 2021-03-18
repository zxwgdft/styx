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
@ApiModel(description = "用户信息")
public class OrgUser extends DeletedBaseModel {

    public static final int USER_TYPE_APP_ADMIN = 9;
    public static final int USER_TYPE_DISTRICT = 3;
    public static final int USER_TYPE_STATION = 2;
    public static final int USER_TYPE_PERSONNEL = 1;

    @TableId
    private String id;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("用户类型")
    private Integer type;

    @ApiModelProperty("手机")
    private String cellphone;

    @ApiModelProperty("站点ID")
    private String stationId;

    @ApiModelProperty("区域编码")
    private String districtCode;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("角色ID")
    private String role;
}