package com.paladin.monitor.service.variable.dto;

import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.QueryCondition;
import com.paladin.framework.service.QueryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author: cxt
 * @time: 2021/2/2
 */
@Getter
@Setter
@ApiModel(description = "组态模板管理")
public class ModuleConfigTemplateQuery extends OffsetPage {
    @QueryCondition(type = QueryType.EQUAL)
    @ApiModelProperty("模组号")
    private Integer moduleNo;
}
