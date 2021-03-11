package com.paladin.monitor.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author cxt
 * @date 2020/10/28
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentTreeVO {
    private String id;

    @ApiModelProperty("部门名称")
    private String name;

    @ApiModelProperty("下属部门")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<DepartmentTreeVO> children;

    public DepartmentTreeVO singleDepartment(String id, String name) {
        this.id = id;
        this.name = name;
        return this;
    }
}
