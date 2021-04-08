package com.styx.common.api;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/3/16
 */
@Getter
@Setter
public abstract class DeletedBaseModel extends BaseModel {

    @TableField(select = false)
    @ApiModelProperty(hidden = true)
    private Boolean deleted;
}
