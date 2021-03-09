package com.styx.data.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/1/5
 */
@Getter
@Setter
@ApiModel(description = "终端数据查询")
public class VariablePassStatusQuery {

    @ApiModelProperty("终端ID")
    private Integer terminalId;
    @ApiModelProperty("变量ID集合")
    private List<Integer> variableIds;
    @ApiModelProperty("开始天")
    private Integer startDay;
    @ApiModelProperty("结束天")
    private Integer endDay;

}
