package com.paladin.monitor.model.config;

import com.baomidou.mybatisplus.annotation.TableId;
import com.styx.common.api.BaseModel;
import com.styx.common.api.DeletedBaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "报警设置信息")
public class ConfigAlarm extends DeletedBaseModel {

    @TableId
    private Integer id;

    @ApiModelProperty("报警名称")
    private String name;

    @ApiModelProperty("报警表达式")
    private String express;

    @ApiModelProperty("变量集合")
    private String variableList;

    @ApiModelProperty("通知目标")
    private String noticeTarget;

    @ApiModelProperty("使用状态")
    private Boolean enabled;


}
