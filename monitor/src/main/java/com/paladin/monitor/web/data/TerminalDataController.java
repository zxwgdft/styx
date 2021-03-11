package com.paladin.monitor.web.data;

import com.paladin.framework.service.PageResult;
import com.paladin.monitor.service.data.TerminalDataService;
import com.paladin.monitor.service.data.dto.*;
import com.paladin.monitor.service.config.dto.StationDeviceQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return terminalDataService.getRealTimeData(serverNode, terminalIds, true);
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

    @ApiOperation(value = "获取终端列表及其实时数据")
    @PostMapping("/realtime/terminal/detail")
    public TerminalListRealtime findTerminalListRealTime(@RequestBody StationDeviceQuery query) {
        return terminalDataService.findTerminalListRealTime(query);
    }

    @ApiOperation(value = "获取终端列表及其实时数据")
    @PostMapping("/realtime/terminal/simple")
    public PageResult<TerminalSimpleRealtime> findTerminalListRealTimeSimple(@RequestBody StationDeviceQuery query) {
        return terminalDataService.findSimpleTerminalListRealTime(query);
    }

    @ApiOperation(value = "获取终端设备详情与实时数据")
    @GetMapping("/realtime/single/detail")
    public TerminalDetailRealtime getTerminalDetailOfStation(@RequestParam int terminalId) {
        return terminalDataService.findTerminalDetailRealtime(terminalId);
    }

    @ApiOperation(value = "获取终端设备历史数据")
    @PostMapping("/history")
    public String getTerminalDetailOfStation(@RequestBody TerminalDataQuery query) {
        return terminalDataService.getTerminalHistoryData(query);
    }

}
