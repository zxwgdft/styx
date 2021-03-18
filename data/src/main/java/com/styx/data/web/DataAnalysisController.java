package com.styx.data.web;

import com.styx.data.core.terminal.TerminalManager;
import com.styx.data.service.DataAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/1/4
 */
@Api(tags = "终端统计数据接口")
@RestController
@RequestMapping("/terminal/analysis")
public class DataAnalysisController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Autowired
    private TerminalManager terminalManager;

    @ApiOperation("获取所有终端累计流量")
    @GetMapping("/flow/all")
    public Map<Integer, Float> getTotalFlowMap() {
        return dataAnalysisService.getTotalFlowMap();
    }

    @ApiOperation("获取终端累计流量")
    @GetMapping("/flow/terminal")
    public Float getTotalFlowOfTerminal(@RequestParam int terminalId) {
        return dataAnalysisService.getTotalFlowOfTerminal(terminalId);
    }

    @ApiOperation("获取时间段内终端累计流量")
    @GetMapping("/flow/terminal/time")
    public Float getTotalFlowOfTerminalByTime(@RequestParam int terminalId, @RequestParam int startDay, @RequestParam int endDay) {
        return dataAnalysisService.getTotalFlowOfTerminalOfTime(terminalId, startDay, endDay);
    }

}
