package com.paladin.monitor.service.variable.dto;

import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.QueryCondition;
import com.paladin.framework.service.QueryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariableQuery extends OffsetPage {

    @QueryCondition(type = QueryType.LIKE)
    private String name;

    @QueryCondition(type = QueryType.EQUAL)
    private Integer type;

    @QueryCondition(type = QueryType.EQUAL)
    private String templateId;
}
