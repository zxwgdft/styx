package com.paladin.monitor.model.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.styx.common.api.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(description = "站点信息")
public class ConfigStation extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("站点名称")
    private String name;

    @ApiModelProperty("拼音名称")
    private String pinyinName;

    @ApiModelProperty("站点地址")
    private String address;

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

    @ApiModelProperty("节点服务器所属")
    private String serverNode;

    @ApiModelProperty("机构等级")
    private Integer agencyLevel;

    @ApiModelProperty("使用状态")
    private Boolean enabled;

    @ApiModelProperty("是否第三方运维")
    private Boolean isThird;

    @ApiModelProperty("是否测试站点")
    private Boolean isTest;

    @ApiModelProperty("排序号")
    private Integer orderNo;
}