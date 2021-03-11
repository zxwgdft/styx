package com.paladin.monitor.service.sys.dto;

import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.QueryCondition;
import com.paladin.framework.service.QueryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "操作日志查询条件")
public class SysLoggerOperateQuery extends OffsetPage {

    @ApiModelProperty("操作人名称")
    @QueryCondition(type=QueryType.LIKE)
    private String operateByName;

    @ApiModelProperty("模块名称")
    @QueryCondition(type=QueryType.LIKE)
    private String modelName;

    @ApiModelProperty("操作名称")
    @QueryCondition(type=QueryType.LIKE)
    private String operateName;

}