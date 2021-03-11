package com.paladin.monitor.service.org.dto;

import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.QueryCondition;
import com.paladin.framework.service.QueryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "角色查询条件")
public class OrgRoleQueryDTO extends OffsetPage {

    @ApiModelProperty("角色名称")
    @QueryCondition(type = QueryType.LIKE)
    private String roleName;

}