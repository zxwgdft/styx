package com.paladin.monitor.service.config.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel(description = "报警设置信息")
public class AlarmDTO {

    private Integer id;

    @ApiModelProperty("报警类型")
    @NotBlank(message = "报警类型不能为空")
    private String type;

    @ApiModelProperty("报警逻辑")
    @NotBlank(message = "报警逻辑不能为空")
    private String logic;

    @ApiModelProperty("通知目标")
    private String noticeTarget;

    @ApiModelProperty("使用状态")
    @NotNull(message = "使用状态不能为空")
    private Boolean enabled;

}
