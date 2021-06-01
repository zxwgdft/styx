package com.styx.data.web;

import com.styx.data.core.terminal.AlarmStatus;
import com.styx.data.service.TerminalDataService;
import com.styx.data.service.dto.DataRecord;
import com.styx.data.service.dto.HistoryDataQuery;
import com.styx.data.service.vo.TerminalAlarms;
import com.styx.data.service.vo.TerminalRealData;
import com.styx.data.service.vo.TerminalSimpleRealData;
import com.styx.data.service.vo.TerminalsSurvey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/11/16
 */
@Api(tags = "终端实时数据")
@RestController
@RequestMapping("/terminal/data")
public class TerminalDataController {

    @Autowired
    private TerminalDataService terminalDataService;

    @ApiOperation("获取终端简要实时数据")
    @GetMapping("/get/real/simple")
    public List<TerminalSimpleRealData> getTerminalSimpleDataRealtime() {
        return terminalDataService.getSimpleRealData();
    }

    @ApiOperation("获取终端实时数据")
    @GetMapping("/get/real/detail")
    public TerminalRealData getTerminalDataRealtime(@RequestParam int terminalId) {
        return terminalDataService.getDetailRealData(terminalId);
    }

    @ApiOperation("获取所有终端报警数据")
    @GetMapping("/get/alarm/all")
    public List<TerminalAlarms> getAllTriggeringAlarms() {
        return terminalDataService.getAlarmStatuses();
    }

    @ApiOperation("获取终端报警数据")
    @GetMapping("/get/alarm")
    public List<AlarmStatus> getTerminalTriggeringAlarms(@RequestParam int terminalId) {
        return terminalDataService.getAlarmStatuses(terminalId);
    }

    @ApiOperation("获取终端历史数据")
    @PostMapping("/find/history")
    public List<DataRecord> getTerminalHistory(@RequestBody HistoryDataQuery query) {
        return terminalDataService.findHistoryData(query);
    }

}
