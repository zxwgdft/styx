package com.paladin.monitor.service.config.dto;

import com.paladin.framework.service.OffsetPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 报警处理查询
 */
@Getter
@Setter
@ApiModel(description = "报警处理查询")
public class AlarmHistoryQuery extends OffsetPage {
    @ApiModelProperty("站点id")
    private String stationId;

    @ApiModelProperty("终端id")
    private String stationDeviceId;

    @ApiModelProperty("是否解决")
    private Boolean isSolved;
}