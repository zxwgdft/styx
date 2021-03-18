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
@ApiModel(description = "终端信息")
public class ConfigTerminal extends DeletedBaseModel {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("站点id")
    private Integer stationId;

    @ApiModelProperty("终端名称")
    private String name;

    @ApiModelProperty("终端UID")
    private String uid;

    @ApiModelProperty("终端类型")
    private Integer type;

    @ApiModelProperty("使用状态")
    private Boolean enabled;

    @ApiModelProperty("参数ids")
    private String variableIds;

    @ApiModelProperty("报警ids")
    private String alarmIds;

}
