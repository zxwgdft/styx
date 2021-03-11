package com.paladin.monitor.service.config.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: cxt
 * @time: 2021/1/22
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoManagementDTO {
    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("url")
    private String url;

    @ApiModelProperty("视频名称")
    private String name;

    @ApiModelProperty("终端id")
    private Integer stationDeviceId;
}
