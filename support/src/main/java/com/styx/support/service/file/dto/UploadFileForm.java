package com.styx.support.service.file.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author TontoZhou
 * @since 2020/8/28
 */
@Getter
@Setter
@ApiModel("上传附件")
public class UploadFileForm {

    @ApiModelProperty("文件")
    private MultipartFile[] files;

    @ApiModelProperty("是否创建图片缩略图")
    private boolean needThumbnail;

    @ApiModelProperty("缩略图宽度")
    private Integer thumbnailWidth;

    @ApiModelProperty("缩略图高度")
    private Integer thumbnailHeight;

    @ApiModelProperty("微信端文件名称")
    private String wxFileName;
}
