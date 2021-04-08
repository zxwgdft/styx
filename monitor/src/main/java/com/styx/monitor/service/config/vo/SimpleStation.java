package com.styx.monitor.service.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "简单站点信息")
public class SimpleStation {

    @ApiModelProperty("站点ID")
    private Integer id;

    @ApiModelProperty("站点名称")
    private String name;

    @ApiModelProperty("站点名称拼音首字母")
    private String pinyinName;

    @ApiModelProperty("站点地区编码")
    private Integer districtCode;

}
