package com.styx.monitor.model.sys;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(description = "账号")
public class SysUser implements Serializable {

    /**
     * 启用状态
     */
    public final static Integer STATE_ENABLED = 1;
    /**
     * 停用状态
     */
    public final static Integer STATE_DISABLED = 0;
    /**
     * 管理员账号
     */
    public final static Integer USER_TYPE_ADMIN = 1;
    /**
     * 人员账号
     */
    public final static Integer USER_TYPE_PERSONNEL = 3;


    public static final String FIELD_ACCOUNT = "account";
    public static final String FIELD_USER_ID = "userId";

    @TableId
    private String id;
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("盐")
    private String salt;
    @ApiModelProperty("关联人员ID")
    private String userId;
    @ApiModelProperty("账号类型")
    private Integer userType;
    @ApiModelProperty("状态")
    private Integer state;
    @ApiModelProperty("最近一次登录时间")
    private Date lastLoginTime;
    @ApiModelProperty("创建时间")
    private Date createTime;

}
