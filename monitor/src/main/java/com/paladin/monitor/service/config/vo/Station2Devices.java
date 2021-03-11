package com.paladin.monitor.service.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/1/12
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "站点到终端树")
public class Station2Devices {

    @ApiModelProperty("站点名称")
    private String name;
    @ApiModelProperty("站点下终端")
    private List<Station2Device> devices;

}
