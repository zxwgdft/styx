package com.paladin.monitor.service.config.dto;

import com.styx.common.service.PageParam;
import com.styx.common.service.QueryType;
import com.styx.common.service.annotation.QueryCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariableQuery extends PageParam {

    @QueryCondition(type = QueryType.LIKE)
    private String name;

    @QueryCondition(type = QueryType.EQUAL)
    private Integer type;

}
