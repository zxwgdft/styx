package com.styx.monitor.service.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/12/7
 */
@Getter
@Setter
@ApiModel(description = "终端数据查询")
public class TerminalDataQuery {

    @ApiModelProperty("终端ID")
    private int terminalId;
    @ApiModelProperty("开始日期")
    private Date startDate;
    @ApiModelProperty("结束日期")
    private Date endDate;
    @ApiModelProperty("变量ID集合")
    private List<Integer> variableIds;

}
