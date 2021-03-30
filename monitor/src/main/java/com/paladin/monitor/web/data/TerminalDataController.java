package com.paladin.monitor.web.data;

import com.paladin.monitor.service.data.TerminalDataService;
import com.paladin.monitor.service.data.dto.TerminalAlarms;
import com.paladin.monitor.service.data.dto.TerminalDetailRealtime;
import com.paladin.monitor.service.data.dto.TerminalRealtime;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(tags = "终端数据接口")
@RestController
@RequestMapping("/monitor/data")
public class TerminalDataController {

    @Autowired
    private TerminalDataService terminalDataService;

    @ApiOperation(value = "获取终端实时数据")
    @GetMapping("/realtime")
    public List<TerminalRealtime> getRealtimeData(@RequestParam String serverNode, @RequestParam(required = false) String terminalIds) {
        return terminalDataService.getRealTimeData(serverNode, terminalIds);
    }

    @ApiOperation(value = "获取单个终端实时数据")
    @GetMapping("/realtime/single")
    public TerminalRealtime getRealtimeData(@RequestParam int terminalId) {
        return terminalDataService.getRealTimeData(terminalId);
    }

    @ApiOperation(value = "获取节点服务所有终端报警数据")
    @GetMapping("/alarm/node")
    public List<TerminalAlarms> getNodeAlarmData(@RequestParam String serverNode) {
        return terminalDataService.getAlarms(serverNode, true);
    }

    @ApiOperation(value = "获取终端报警数据")
    @GetMapping("/alarm/terminal")
    public TerminalAlarms getTerminalAlarmData(@RequestParam int terminalId) {
        return terminalDataService.getTerminalAlarms(terminalId);
    }


    @ApiOperation(value = "获取终端设备详情与实时数据")
    @GetMapping("/realtime/single/detail")
    public TerminalDetailRealtime getTerminalDetailOfStation(@RequestParam int terminalId) {
        return terminalDataService.findTerminalDetailRealtime(terminalId);
    }


}
