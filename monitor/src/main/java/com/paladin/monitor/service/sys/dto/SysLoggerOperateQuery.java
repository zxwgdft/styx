package com.paladin.monitor.service.sys.dto;

import com.styx.common.service.PageParam;
import com.styx.common.service.QueryType;
import com.styx.common.service.annotation.QueryCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "操作日志查询条件")
public class SysLoggerOperateQuery extends PageParam {

    @ApiModelProperty("操作人名称")
    @QueryCondition(type = QueryType.EQUAL)
    private String operateByName;

    @ApiModelProperty("模块名称")
    @QueryCondition(type = QueryType.EQUAL)
    private String modelName;

}