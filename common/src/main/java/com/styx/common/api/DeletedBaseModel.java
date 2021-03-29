package com.styx.common.api;

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

    @ApiModelProperty("是否删除")
    private Boolean deleted;
}
