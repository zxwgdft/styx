package com.paladin.monitor.model.config;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ConfigNode {

    @TableId
    @ApiModelProperty("节点编码")
    private String code;

    @ApiModelProperty("节点名称")
    private String name;

    @ApiModelProperty("是否默认节点")
    private Boolean isDefault;

}