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
@ApiModel("站点终端设备查询")
public class StationDeviceQuery extends OffsetPage {

    @ApiModelProperty("站点名称")
    @QueryCondition(type = QueryType.LIKE)
    private String name;

    @ApiModelProperty("站点ID")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer stationId;

    @ApiModelProperty("省编码")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer provinceCode;

    @ApiModelProperty("区市编码")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer cityCode;

    @ApiModelProperty("区县编码")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer districtCode;

    @ApiModelProperty("机构等级")
    @QueryCondition(type = QueryType.EQUAL)
    private Integer agencyLevel;

    @ApiModelProperty("是否启用")
    @QueryCondition(type = QueryType.EQUAL)
    private Boolean enabled;

    @ApiModelProperty("服务节点")
    private String serverNode;

    @ApiModelProperty("是否测试站点")
    @QueryCondition(type = QueryType.EQUAL)
    private Boolean isTest;
}
