package com.paladin.monitor.service.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "简单站点信息")
public class SimpleStationVO {

    private Integer id;

    @ApiModelProperty("站点名称")
    private String name;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty("省")
    private Integer provinceCode;

    @ApiModelProperty("市")
    private Integer cityCode;

    @ApiModelProperty("区县")
    private Integer districtCode;

}
