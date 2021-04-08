package com.styx.monitor.core.distrcit;

import com.styx.common.service.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/4/8
 */
@Getter
@Setter
public class DistrictQuery extends PageParam {

    @ApiModelProperty(hidden = true)
    private Integer provinceCode;

    @ApiModelProperty(hidden = true)
    private Integer cityCode;

    @ApiModelProperty("地区编码")
    private Integer districtCode;

}
