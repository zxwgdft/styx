package com.paladin.monitor.service.config.dto;

import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.QueryCondition;
import com.paladin.framework.service.QueryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "站点查询条件")
public class StationPageQuery extends OffsetPage {

    @ApiModelProperty("站点名称")
    @QueryCondition(type = QueryType.LIKE)
    private String name;

    @ApiModelProperty("省编码")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer provinceCode;

    @ApiModelProperty("市编码")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer cityCode;

    @ApiModelProperty("区县编码")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer districtCode;

    @ApiModelProperty("是否测试站点")
    @QueryCondition(type = QueryType.EQUAL)
    private Boolean isTest;

    @ApiModelProperty("是否启用")
    @QueryCondition(type = QueryType.EQUAL)
    private Boolean enabled;

    @ApiModelProperty("机构等级")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer agencyLevel;
}
