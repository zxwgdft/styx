package com.paladin.monitor.service.config.dto;

import com.styx.common.service.PageParam;
import com.styx.common.service.QueryType;
import com.styx.common.service.annotation.QueryCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "报警配置查询条件")
public class AlarmQuery extends PageParam {

    @ApiModelProperty("报警类型名称")
    @QueryCondition(type = QueryType.LIKE)
    private String type;
}
