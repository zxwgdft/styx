package com.paladin.monitor.service.config.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@ApiModel
public class AlarmHistoryDTO {

    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("终端ID")
    @NotNull(message = "终端ID不能为空")
    private Integer terminalId;

    @ApiModelProperty("报警ID")
    @NotNull(message = "报警ID不能为空")
    private Integer alarmId;

    @ApiModelProperty("开始时间")
    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @NotNull(message = "结束时间不能为空")
    private Date endTime;

    @ApiModelProperty("是否关闭")
    @NotNull(message = "是否关闭不能为空")
    private Boolean closed;

    @ApiModelProperty("处理方式")
    @NotNull(message = "处理方式不能为空")
    private Integer processingMethod;

    @ApiModelProperty("处理描述")
    private String description;

    @ApiModelProperty("是否解决")
    @NotNull(message = "是否解决不能为空")
    private Boolean solved;

    @ApiModelProperty("现场照片地址")
    private String photo;
}