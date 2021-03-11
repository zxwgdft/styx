package com.paladin.monitor.service.sys.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cxt
 * @date 2020/9/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private String id;

    @ApiModelProperty("部门名称")
    private String name;

    @ApiModelProperty("父级id")
    private String pid;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态")
    private Boolean state;
}
