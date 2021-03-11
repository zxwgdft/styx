package com.paladin.monitor.model.config;

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
@ApiModel(description = "报警设置信息")
public class ConfigAlarm extends BaseModel {

    @TableId
    private Integer id;

    @ApiModelProperty("报警类型")
    private String type;

    @ApiModelProperty("报警逻辑")
    private String logic;

    @ApiModelProperty("变量集合")
    private String variableList;

    @ApiModelProperty("通知目标")
    private String noticeTarget;

    @ApiModelProperty("使用状态")
    private Boolean enabled;


}
