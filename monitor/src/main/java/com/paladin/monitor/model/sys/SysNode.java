package com.paladin.monitor.model.sys;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SysNode {

    @TableId
    @ApiModelProperty("节点编码")
    private String code;

    @ApiModelProperty("节点名称")
    private String name;

    @ApiModelProperty("父节点编码")
    private String parentCode;

    @ApiModelProperty("是否默认节点")
    private Boolean isDefault;

}