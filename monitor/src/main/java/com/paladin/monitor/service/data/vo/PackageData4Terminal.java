package com.paladin.monitor.service.data.vo;

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
@ApiModel(description = "用于站点终端分析数据包")
public class PackageData4Terminal {
    @ApiModelProperty("总工作时间（分钟）")
    private Integer totalWorkTime;
    @ApiModelProperty("总流量")
    private Float totalFlow;
    @ApiModelProperty("变量数据合格情况")
    private List<VariablePassStatus> passStatus;

}
