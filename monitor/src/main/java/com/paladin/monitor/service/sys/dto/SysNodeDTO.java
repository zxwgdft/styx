package com.paladin.monitor.service.sys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ApiModel
public class SysNodeDTO {


    @ApiModelProperty("节点编码")
    @NotEmpty(message = "节点编码不能为空")
    @Length(max = 20, message = "节点编码长度不能大于20")
    private String code;

    @ApiModelProperty("节点名称")
    @NotEmpty(message = "节点名称不能为空")
    @Length(max = 30, message = "节点名称长度不能大于30")
    private String name;

    @ApiModelProperty("父节点编码")
    @Length(max = 20, message = "父节点编码长度不能大于20")
    private String parentCode;
}