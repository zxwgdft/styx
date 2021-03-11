package com.paladin.monitor.service.sys.dto;

import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.QueryCondition;
import com.paladin.framework.service.QueryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysConstantQuery extends OffsetPage {

    @QueryCondition(type = QueryType.LIKE)
    private String type;

    @QueryCondition(type = QueryType.LIKE)
    private String name;

    @QueryCondition(type = QueryType.LIKE)
    private String description;

}
