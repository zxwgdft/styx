package com.styx.data.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author TontoZhou
 * @since 2020/12/4
 */
@Getter
@Setter
@ApiModel(description = "节点上传数据")
public class Data4Upload {

    @ApiModelProperty("ID")
    private Integer id;
    @ApiModelProperty("终端ID")
    private Integer terminalId;
    @ApiModelProperty("是否在线")
    private Boolean isOnline;
    @ApiModelProperty("工作状态")
    private Integer workStatus;
    @ApiModelProperty("采集时间")
    private Date createTime;
    @ApiModelProperty("日期")
    private Integer day;
    @ApiModelProperty("小时")
    private Integer hour;
    @ApiModelProperty("值")
    private String values;
}
