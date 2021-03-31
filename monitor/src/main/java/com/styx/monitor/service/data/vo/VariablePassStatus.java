package com.styx.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/1/5
 */
@Getter
@Setter
@ApiModel(description = "变量数据合格情况")
public class VariablePassStatus {

    @ApiModelProperty("变量ID")
    private Integer variableId;
    @ApiModelProperty("变量名称")
    private String variableName;
    @ApiModelProperty("总数")
    private Integer totalNum;
    @ApiModelProperty("合格数")
    private Integer passNum;

}
