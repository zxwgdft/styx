package com.styx.monitor.service.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@ApiModel(description = "用户信息")
public class OrgUserDTO {

    private String id;

    @ApiModelProperty("用户类型")
    @NotNull(message = "用户类型不能为空")
    private Integer type;

    @ApiModelProperty("用户姓名")
    @NotBlank(message = "用户名称不能为空")
    private String name;

    @ApiModelProperty("手机")
    private String cellphone;

    @ApiModelProperty("站点ID")
    private String stationId;

    @ApiModelProperty("区域编码")
    private String districtCode;

    @ApiModelProperty("账号")
    @NotBlank(message = "账号名称不能为空")
    private String account;

    @ApiModelProperty("角色")
    @NotBlank(message = "角色不能为空")
    private String role;

}
