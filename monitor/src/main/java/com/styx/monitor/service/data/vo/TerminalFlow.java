package com.styx.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/5/10
 */
@Getter
@Setter
@ApiModel(description = "终端累计流量信息")
public class TerminalFlow {

    @ApiModelProperty("终端ID")
    private int id;
    @ApiModelProperty("终端名称")
    private String name;
    @ApiModelProperty("站点名称")
    private String stationName;
    @ApiModelProperty("累计流量")
    private double flow;

}
