package com.paladin.monitor.service.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author cxt
 * @date 2020/10/23
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentVO {
    private String id;

    @ApiModelProperty("部门名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("父级id")
    private String pid;

    @ApiModelProperty("父级名称")
    private String parentName;

    @ApiModelProperty("状态")
    private Boolean state;

}
