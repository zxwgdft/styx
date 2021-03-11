package com.paladin.monitor.service.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/1/5
 */
@Getter
@Setter
@ApiModel(description = "管理数据分析查询")
public class PackageManagerQuery {

    @ApiModelProperty("地区ID")
    private Integer districtId;


}
