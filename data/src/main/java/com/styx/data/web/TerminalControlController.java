package com.styx.data.web;

import com.styx.common.api.R;
import com.styx.common.utils.StringUtil;
import com.styx.data.core.terminal.TerminalManager;
import com.styx.data.service.TerminalDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/11/16
 */
@Api(tags = "终端控制接口")
@RestController
@RequestMapping("/terminal/control")
public class TerminalControlController {

    @Autowired
    private TerminalManager terminalManager;

    @Autowired
    private TerminalDataService terminalDataService;

    @ApiOperation("终端开始维护")
    @GetMapping("/maintain/start")
    public R startMaintain(@RequestParam int terminalId, @RequestParam int duration) {
        terminalManager.startMaintain(terminalId, duration);
        return R.success();
    }

    @ApiOperation("终端结束维护")
    @GetMapping("/maintain/off")
    public R offMaintain(@RequestParam int terminalId) {
        terminalManager.offMaintain(terminalId);
        return R.success();
    }




}
