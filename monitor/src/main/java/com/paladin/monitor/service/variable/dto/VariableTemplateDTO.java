package com.paladin.monitor.service.variable.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "变量模板信息")
public class VariableTemplateDTO {

    private Integer  id;

    // 模板名称
    @NotBlank(message = "模板名称不能为空")
    @Length(max = 50, message = "模板名称长度不能大于50")
    private String name;

    //模板描述
    private String description;

    // 是否启用 1是0否
    private Boolean enabled;

    private String variableIds;
}
