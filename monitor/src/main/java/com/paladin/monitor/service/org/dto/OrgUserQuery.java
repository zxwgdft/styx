package com.paladin.monitor.service.org.dto;

import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.QueryCondition;
import com.paladin.framework.service.QueryType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <>
 *
 * @author Huangguochen
 * @create 2020/4/24 15:40
 */
@Getter
@Setter
public class OrgUserQuery extends OffsetPage {

    @QueryCondition(type = QueryType.LIKE)
    private String name;

    @QueryCondition(type = QueryType.IN)
    private List<String> department;


}
