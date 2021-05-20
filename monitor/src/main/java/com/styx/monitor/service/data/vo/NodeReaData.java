package com.styx.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/5/20
 */
@Getter
@Setter
@ApiModel(description = "节点实时数据")
public class NodeReaData {

    @ApiModelProperty("节点编码")
    private String code;

    @ApiModelProperty("节点名称")
    private String name;

    @ApiModelProperty("是否在线")
    private boolean online;
}
