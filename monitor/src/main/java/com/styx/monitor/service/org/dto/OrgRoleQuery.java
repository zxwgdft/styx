package com.styx.monitor.service.org.dto;

import com.styx.common.service.PageParam;
import com.styx.common.service.QueryType;
import com.styx.common.service.annotation.QueryCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "角色查询条件")
public class OrgRoleQuery extends PageParam {

    @ApiModelProperty("角色名称")
    @QueryCondition(type = QueryType.LIKE)
    private String roleName;

}