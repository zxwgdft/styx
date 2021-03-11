package com.paladin.monitor.service.variable.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "变量信息")
public class VariableVO {

    private Integer id;

    @ApiModelProperty("变量名称")
    private String name;

    @ApiModelProperty("变量类型")
    private Integer type;

    @ApiModelProperty("变量标识")
    private Integer tag;

    @ApiModelProperty("变量单位")
    private String unit;

    @ApiModelProperty("变量最小值")
    private Float min;

    @ApiModelProperty("变量最大值")
    private Float max;

    @ApiModelProperty("数据地址起始位置")
    private Integer startPosition;

    @ApiModelProperty("开关量数据地址")
    private Integer switchPosition;

    @ApiModelProperty("是否启用")
    private Boolean enabled;

    @ApiModelProperty("是否展示")
    private Boolean showed;

    @ApiModelProperty("是否持久化")
    private Boolean persisted;

}
