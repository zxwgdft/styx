package com.paladin.monitor.service.org.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <>
 *
 * @author Huangguochen
 * @create 2020/4/24 15:41
 */
@Getter
@Setter
@ApiModel(description = "用户信息")
public class OrgUserVO {

    private String id;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("用户类型")
    private Integer type;

    @ApiModelProperty("所属部门")
    private String department;

    @ApiModelProperty("手机")
    private String cellphone;

    @ApiModelProperty("所属机构")
    private Integer agency;

    @ApiModelProperty("站点ID")
    private String stationId;

    @ApiModelProperty("区域编码")
    private String districtCode;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("角色ID")
    private String role;
    

}
