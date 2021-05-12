package com.styx.monitor.service.message.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/5/12
 */
@Getter
@Setter
@ApiModel(description = "终端事件消息信息")
public class TerminalMessage {

    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("终端名称")
    private String name;
    @ApiModelProperty("站点名称")
    private String stationName;
    @ApiModelProperty("消息时间")
    private long time;

}
