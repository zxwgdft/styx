package com.styx.support.service.file.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2020/8/28
 */
@Getter
@Setter
@ApiModel("上传附件")
public class UploadFileBase64 {

    @ApiModelProperty("文件名称")
    private String filename;
    @ApiModelProperty("文件大小")
    private String base64str;

    @ApiModelProperty("是否创建图片缩略图")
    private boolean needThumbnail;

    @ApiModelProperty("缩略图宽度")
    private Integer thumbnailWidth;
    @ApiModelProperty("缩略图高度")
    private Integer thumbnailHeight;

}
