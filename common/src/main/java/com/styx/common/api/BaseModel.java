package com.styx.common.api;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public abstract class BaseModel implements Serializable {

    @ApiModelProperty("创建时间")
    private Date createTime;

    @TableField(select = false)
    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @TableField(select = false)
    @ApiModelProperty("更新者")
    private String updateBy;

}
