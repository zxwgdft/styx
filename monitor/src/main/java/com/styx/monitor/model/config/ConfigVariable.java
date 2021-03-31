package com.styx.monitor.model.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.styx.common.api.DeletedBaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(description = "变量信息")
public class ConfigVariable extends DeletedBaseModel {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("变量名称")
    private String name;

    @ApiModelProperty("变量类型")
    private Integer type;

    @ApiModelProperty("变量单位")
    private String unit;

    @ApiModelProperty("变量最小值")
    private Integer min;

    @ApiModelProperty("变量最大值")
    private Integer max;

    @ApiModelProperty("规模")
    private Integer scale;

    @ApiModelProperty("数据字节位置")
    private Integer bytePosition;

    @ApiModelProperty("数据bit位置")
    private Integer bitPosition;

    @ApiModelProperty("是否启用")
    private Boolean enabled;

    @ApiModelProperty("是否持久化")
    private Boolean persisted;


}
