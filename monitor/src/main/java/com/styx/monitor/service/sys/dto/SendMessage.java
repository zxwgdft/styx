package com.styx.monitor.service.sys.dto;

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
@ApiModel("发送短信")
public class SendMessage {

    @ApiModelProperty("手机号码")
    private List<String> cellphones;
    @ApiModelProperty("短信内容")
    private String content;

}
