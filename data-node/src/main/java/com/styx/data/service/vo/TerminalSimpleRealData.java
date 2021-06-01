package com.styx.data.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2020/11/16
 */
@Getter
@Setter
@ApiModel(description = "终端简要实时数据")
public class TerminalSimpleRealData {

    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("是否在线")
    private boolean isOnline;
    @ApiModelProperty("是否报警中")
    private boolean isAlarmTriggering;
}
