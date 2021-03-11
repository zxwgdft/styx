package com.paladin.monitor.model.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.styx.common.api.BaseModel;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(description = "变量信息")
public class ConfigVariable extends BaseModel {

    public static final String FIELD_ENABLED = "enabled";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SHOWED = "showed";

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 变量名称
    private String name;

    // 变量类型
    private Integer type;

    // 变量标识
    private Integer tag;

    // 变量单位
    private String unit;

    // 变量最小值
    private Float min;

    // 变量最大值
    private Float max;

    // 规模
    private Integer scale;

    //数据地址起始位置
    private Integer startPosition;

    // 开关量数据地址
    private Integer switchPosition;

    // 是否启用
    private Boolean enabled;

    // 是否展示
    private Boolean showed;

    // 是否持久化
    private Boolean persisted;
}
