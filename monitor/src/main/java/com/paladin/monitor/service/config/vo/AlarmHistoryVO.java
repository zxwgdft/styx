package com.paladin.monitor.service.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@ApiModel
public class AlarmHistoryVO {

    @ApiModelProperty("")
    private Long id;

	@ApiModelProperty("站点名称")
	private String stationName;

    @ApiModelProperty("终端名称")
    private String terminalName;

    @ApiModelProperty("报警类型")
    private String alarmIdType;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("是否关闭")
    private Boolean closed;

    @ApiModelProperty("处理方式")
    private Integer processingMethod;

    @ApiModelProperty("处理描述")
    private String description;

    @ApiModelProperty("是否解决")
    private Boolean solved;

    @ApiModelProperty("处理人")
    private String handleName;

    @ApiModelProperty("现场照片地址")
    private String photo;

    @ApiModelProperty("是否审核")
    private Boolean isExamine;

    @ApiModelProperty("驳回理由")
    private String rejection;
}