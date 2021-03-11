package com.paladin.monitor.service.variable.vo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "变量模板信息")
public class VariableTemplateVO {

    private Integer id;

    // 模板名称
    private String name;

    //模板描述
    private String description;

    // 是否启用 1是0否
    private Boolean enabled;

    //创建人
    private String createBy;

    //创建时间
    private Date createTime;

    //修改人
    private String updateBy;

    //修改时间
    private Date updateTime;

    private String variableIds;


}
