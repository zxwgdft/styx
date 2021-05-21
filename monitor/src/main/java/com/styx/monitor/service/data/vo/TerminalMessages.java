package com.styx.monitor.service.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/5/12
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "终端多个事件消息信息")
public class TerminalMessages {
    @ApiModelProperty("上线终端消息")
    private List<TerminalMessage> onlineMessages;
    @ApiModelProperty("下线终端消息")
    private List<TerminalMessage> offlineMessages;
}
