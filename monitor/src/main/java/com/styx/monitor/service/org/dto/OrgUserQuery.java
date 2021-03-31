package com.styx.monitor.service.org.dto;

import com.styx.common.service.PageParam;
import com.styx.common.service.QueryType;
import com.styx.common.service.annotation.QueryCondition;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrgUserQuery extends PageParam {

    @QueryCondition(type = QueryType.LIKE)
    private String name;


}
