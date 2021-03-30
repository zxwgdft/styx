package com.styx.data.web.vo;

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
public class TerminalSimpleRealtime {

    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("是否在线")
    private boolean isOnline;
    @ApiModelProperty("工作状态")
    private int workStatus;
    @ApiModelProperty("是否维护中")
    private boolean isMaintaining;
    @ApiModelProperty("是否报警中")
    private boolean isAlarmTriggering;
}
