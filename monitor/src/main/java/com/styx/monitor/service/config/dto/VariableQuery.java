package com.styx.monitor.service.config.dto;

import com.styx.common.service.PageParam;
import com.styx.common.service.QueryType;
import com.styx.common.service.annotation.QueryCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/4/8
 */
@Getter
@Setter
public class VariableQuery extends PageParam {

    @ApiModelProperty("变量名称")
    @QueryCondition(type = QueryType.LIKE)
    private String name;

    @ApiModelProperty("变量类型")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer type;

    @ApiModelProperty("使用状态")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer enabled;

}
