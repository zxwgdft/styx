package com.styx.monitor.service.config.cache;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/5/7
 */
@Getter
@Setter
@ApiModel(description = "简要报警信息")
public class SimpleVariable {

    private Integer id;

    @ApiModelProperty("变量名称")
    private String name;

    @ApiModelProperty("变量类型")
    private Integer type;

    @ApiModelProperty("变量单位")
    private String unit;

    @ApiModelProperty("变量最小值")
    private Integer min;

    @ApiModelProperty("变量最大值")
    private Integer max;

    @ApiModelProperty("规模")
    private Integer scale;

    @ApiModelProperty("是否启用")
    private Boolean enabled;

}
