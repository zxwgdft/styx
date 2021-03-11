package com.paladin.monitor.model.sys;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;


@Getter
@Setter
@ApiModel(description = "数据字典")
public class SysConstant {

    @Id
    private String type;
    @Id
    private String code;

    private String name;

    private String description;

    private Integer orderNo;


}
