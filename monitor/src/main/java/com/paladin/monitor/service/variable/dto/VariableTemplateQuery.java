package com.paladin.monitor.service.variable.dto;

import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.QueryCondition;
import com.paladin.framework.service.QueryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariableTemplateQuery extends OffsetPage {

    @QueryCondition(type = QueryType.LIKE)
    private String name;
}
