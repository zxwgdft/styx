package com.styx.support.service.file.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/10/22
 */
@Getter
@Setter
@ApiModel("提交操作")
public class CommitOperate {

    @ApiModelProperty("删除文件")
    private List<String> deleteIds;
    @ApiModelProperty("保存文件")
    private List<String> saveIds;
    @ApiModelProperty("操作人")
    private String operateBy;
}
