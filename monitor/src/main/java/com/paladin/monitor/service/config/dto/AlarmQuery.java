package com.paladin.monitor.service.config.dto;

import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.QueryCondition;
import com.paladin.framework.service.QueryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "报警配置查询条件")
public class AlarmQuery extends OffsetPage {

    @ApiModelProperty("报警类型名称")
    @QueryCondition(type = QueryType.LIKE)
    private String type;
}
