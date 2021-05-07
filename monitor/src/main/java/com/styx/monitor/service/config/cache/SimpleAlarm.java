package com.styx.monitor.service.config.cache;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/5/7
 */
@Getter
@Setter
@ApiModel(description = "简要报警信息")
public class SimpleAlarm {

    private Integer id;

    @ApiModelProperty("报警名称")
    private String name;

    @ApiModelProperty("使用状态")
    private Boolean enabled;

}
