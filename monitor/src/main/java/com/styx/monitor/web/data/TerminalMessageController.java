package com.styx.monitor.web.data;

import com.styx.monitor.service.message.TerminalMessageService;
import com.styx.monitor.service.message.vo.TerminalMessages;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "消息接口")
@RestController
@RequestMapping("/monitor/message")
public class TerminalMessageController {

    @Autowired
    private TerminalMessageService terminalMessageService;

}
