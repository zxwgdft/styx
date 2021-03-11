package com.paladin.monitor.service.sys.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author cxt
 * @date 2020/9/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysConstantDTO {

    @ApiModelProperty("分类")
    @NotNull(message = "分类不能为空")
    private String type;

    @ApiModelProperty("code")
    @NotNull(message = "编码code不能为空")
    private String code;

    @ApiModelProperty("name")
    @NotNull(message = "名称name不能为空")
    private String name;

    @ApiModelProperty("描述")
    @NotNull(message = "描述不能为空")
    private String description;

    @ApiModelProperty("序号")
    @NotNull(message = "序号不能为空")
    private Integer orderNo;


}
