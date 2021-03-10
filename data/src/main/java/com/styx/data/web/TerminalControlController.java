package com.styx.data.web;

import com.styx.common.api.R;
import com.styx.common.utils.StringUtil;
import com.styx.data.core.terminal.TerminalManager;
import com.styx.data.service.TerminalDataService;
import com.styx.data.service.dto.MoveTerminalInfo;
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

    @ApiOperation("终端转移到其他节点")
    @GetMapping("/move")
    public R moveNode(@RequestParam String terminalIds, @RequestParam String targetNode) {
        terminalDataService.moveToServerNode(targetNode, StringUtil.stringToIntArray(terminalIds));
        return R.success();
    }

    @ApiOperation("终端信息来到本节点")
    @PostMapping("/come")
    public R comeNode(@RequestBody List<MoveTerminalInfo> data) {
        terminalDataService.saveMovedTerminalInfo(data);
        return R.success();
    }


    @ApiOperation("测试终端转为正式终端")
    @GetMapping("/turn/formal")
    public R turnFormal(@RequestParam String terminalIds) {
        terminalManager.turnFormal(StringUtil.stringToIntList(terminalIds));
        return R.success();
    }

}
