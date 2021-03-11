package com.paladin.monitor.web.data;

import com.paladin.framework.common.R;
import com.paladin.monitor.service.data.TerminalControlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TontoZhou
 * @since 2020/11/16
 */
@Api(tags = "终端控制接口")
@RestController
@RequestMapping("/monitor/terminal/control")
public class TerminalControlController {

    @Autowired
    private TerminalControlService terminalControlService;

    @ApiOperation("终端开始维护")
    @GetMapping("/maintain/start")
    public R startMaintain(@RequestParam int terminalId, @RequestParam int duration) {
        terminalControlService.startMaintain(terminalId, duration);
        return R.success();
    }

    @ApiOperation("终端结束维护")
    @GetMapping("/maintain/off")
    public R offMaintain(@RequestParam int terminalId) {
        terminalControlService.offMaintain(terminalId);
        return R.success();
    }




}
