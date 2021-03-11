package com.paladin.monitor.web.org.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author cxt
 * @date 2020/11/10
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgPermissionTreeVO {
    private String id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("url")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String url;

    @ApiModelProperty("icon")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icon;

    @ApiModelProperty("排序")
    private Integer listOrder;

    @ApiModelProperty("子集")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<OrgPermissionTreeVO> children;

    public OrgPermissionTreeVO init(String id, String name) {
        this.id = id;
        this.name = name;
        return this;
    }
}
