package com.paladin.monitor.service.variable.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel(description = "变量信息")
public class VariableDTO {

    private Integer id;

    @ApiModelProperty("变量名称")
    @NotBlank(message = "变量名称不能为空")
    @Length(max = 50, message = "变量名称长度不能大于50")
    private String name;

    @ApiModelProperty("变量类型")
    @NotNull(message = "变量类型不能为空")
    private Integer type;

    @ApiModelProperty("变量标识")
    @NotNull(message = "变量标识不能为空")
    private Integer tag;

    @ApiModelProperty("变量单位")
    @NotBlank(message = "变量单位不能为空")
    @Length(max = 20, message = "变量单位长度不能大于20")
    private String unit;

    @ApiModelProperty("变量最小值")
    private Float min;

    @ApiModelProperty("变量最大值")
    private Float max;

    @ApiModelProperty("数据地址起始位置")
    @NotNull(message = "数据地址起始位置不能为空")
    private Integer startPosition;

    @ApiModelProperty("开关量数据地址")
    @NotNull(message = "开关量数据地址不能为空")
    private Integer switchPosition;

    @ApiModelProperty("是否启用")
    private Boolean enabled;

    @ApiModelProperty("是否展示")
    private Boolean showed;

    @ApiModelProperty("是否展示")
    private Boolean persisted;
}
