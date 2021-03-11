package com.paladin.monitor.service.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "报警设置信息")
public class AlarmVO {

    private Integer id;

    @ApiModelProperty("报警类型")
    private String type;

    @ApiModelProperty("报警逻辑")
    private String logic;

    @ApiModelProperty("变量集合")
    private String variableList;

    @ApiModelProperty("通知目标")
    private String noticeTarget;

    @ApiModelProperty("使用状态")
    private Boolean enabled;

}
