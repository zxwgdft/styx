package com.paladin.monitor.model.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.styx.common.api.BaseModel;
import com.styx.common.api.DeletedBaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(description = "站点信息")
public class ConfigStation extends DeletedBaseModel {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("站点名称")
    private String name;

    @ApiModelProperty("拼音名称")
    private String pinyinName;

    @ApiModelProperty("站点地址")
    private String address;

    @ApiModelProperty("省")
    private Integer provinceCode;

    @ApiModelProperty("市")
    private Integer cityCode;

    @ApiModelProperty("区县")
    private Integer districtCode;

    @ApiModelProperty("节点服务器所属")
    private String serverNode;

    @ApiModelProperty("使用状态")
    private Boolean enabled;

    @ApiModelProperty("排序号")
    private Integer orderNo;
}