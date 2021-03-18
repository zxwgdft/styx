package com.paladin.monitor.service.config.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel(description = "站点信息")
public class StationEditDTO {

    private Integer id;

    // 站点名称
    @ApiModelProperty("站点名称")
    @NotBlank(message = "站点名称不能为空")
    @Size(max = 50, message = "站点名称长度不能大于50")
    private String name;

    //站点地址
    @ApiModelProperty("站点地址")
    @NotBlank(message = "站点地址不能为空")
    @Size(max = 100, message = "站点地址长度不能大于100")
    private String address;

    //经度
    @ApiModelProperty("经度")
    @NotBlank(message = "经度不能为空")
    private String longitude;

    //纬度
    @ApiModelProperty("纬度")
    @NotBlank(message = "经度不能为空")
    private String latitude;

    @ApiModelProperty("省编码")
    @NotNull(message = "省不能为空")
    private Integer provinceCode;

    @ApiModelProperty("市编码")
    @NotNull(message = "市编码不能为空")
    private Integer cityCode;

    //区县
    @ApiModelProperty("地区编码")
    @NotNull(message = "地区编码不能为空")
    private Integer districtCode;

    //使用状态1是，0否
    @ApiModelProperty("使用状态")
    private Boolean enabled;

    //是否第三方运维1是，0否
    @ApiModelProperty("是否第三方运维")
    private Boolean isThird;

    @ApiModelProperty("机构等级")
    private Integer agencyLevel;

}
