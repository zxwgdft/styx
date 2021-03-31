package com.styx.monitor.service.sys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2020/2/27
 */
@Getter
@Setter
@ApiModel
public class FileResource {

    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("附件名称")
    private String name;
    @ApiModelProperty("附件大小")
    private Long size;
    @ApiModelProperty("附件地址")
    private String url;
    @ApiModelProperty("附件缩略图地址")
    private String thumbnailUrl;

}
