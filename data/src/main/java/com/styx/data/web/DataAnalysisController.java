package com.styx.data.web;

import com.styx.common.exception.BusinessException;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalManager;
import com.styx.data.service.DataAnalysisService;
import com.styx.data.service.vo.VariablePassStatus;
import com.styx.data.web.dto.VariablePassStatusQuery;
import com.styx.data.web.vo.PackageData4Manager;
import com.styx.data.web.vo.PackageData4Terminal;
import com.styx.data.web.vo.TerminalAlarms;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @ApiOperation("获取终端变量数据合格情况")
    @PostMapping("/pass/status/terminal")
    public List<VariablePassStatus> getFlowValueOfTerminal(@RequestBody VariablePassStatusQuery query) {
        return dataAnalysisService.getVariablePassStatusOfTerminal(query.getTerminalId(), query.getStartDay(), query.getEndDay(), query.getVariableIds());
    }


    @ApiOperation("获取终端分析相关数据包")
    @PostMapping("/package/terminal")
    public PackageData4Terminal getPackageData4Terminal(@RequestBody VariablePassStatusQuery query) {
        PackageData4Terminal result = new PackageData4Terminal();
        Terminal terminal = terminalManager.getTerminal(query.getTerminalId());
        if (terminal == null) {
            throw new BusinessException("终端不存在");
        }

        int workTotalMinutes = (int) (terminal.getWorkTotalTime() / 60000);
        result.setTotalWorkTime(workTotalMinutes);

        result.setTotalFlow(dataAnalysisService.getTotalFlowOfTerminal(query.getTerminalId()));
        result.setPassStatus(dataAnalysisService.getVariablePassStatusOfTerminal(query.getTerminalId(), query.getStartDay(), query.getEndDay(), query.getVariableIds()));

        return result;
    }

    @ApiOperation("获取管理分析相关数据包")
    @GetMapping("/package/manager")
    public PackageData4Manager getPackageData4Manager(@RequestParam(required = false) String terminalIds) {
        Set<Integer> terminalIdSet = null;

        // 是否全部终端
        boolean isAll = false;
        Collection<Terminal> terminals = terminalManager.getTerminals();

        if (terminalIds != null && terminalIds.length() > 0) {
            terminalIdSet = new HashSet<>();
            String[] ids = terminalIds.split(",");

            /*
             * 为了效率，这里通过比对终端数目是否一致来判断是否全部。
             * 该判断方式基于传递参数的可靠性（无重复，并且为该节点下的终端）
             * 如果无法保证参数可靠，需要去除该段代码
             */
            if (terminals != null && ids.length == terminals.size()) {
                isAll = true;
            }

            for (String id : ids) {
                terminalIdSet.add(Integer.valueOf(id));
            }
        } else {
            isAll = true;
        }

        PackageData4Manager result = new PackageData4Manager();

        Map<Integer, Long> totalWorkTimeMap = new HashMap<>();

        List<Integer> onlineIds = new ArrayList<>();
        List<Integer> alarmIds = new ArrayList<>();

        List<TerminalAlarms> terminalAlarms = new ArrayList<>();

        if (terminals != null && terminals.size() > 0) {
            for (Terminal terminal : terminals) {
                // 排除测试用的
                if(terminal.isTest()) {
                    continue;
                }
                int terminalId = terminal.getId();
                // 为空则是指定全部，否则需要指定的终端才统计
                if (isAll || terminalIdSet.contains(terminalId)) {
                    totalWorkTimeMap.put(terminalId, terminal.getWorkTotalTime());

                    // 统计在线设备
                    if (terminal.isOnline()) {
                        onlineIds.add(terminalId);

                        // 统计报警设备，只统计在线的
                        if (!terminal.getAlarmTriggeringMap().isEmpty()) {
                            alarmIds.add(terminalId);
                        }
                    }

                    Map<Integer, Terminal.AlarmStatus> map = terminal.getAlarmTriggeringMap();
                    if (map.size() > 0) {
                        terminalAlarms.add(new TerminalAlarms(terminal.getId(), terminal.getName(),
                                terminal.getStationId(), terminal.getStationName(), map.values()));
                    }
                }
            }
        }

        result.setOnlineTerminalIds(onlineIds);
        result.setAlarmTerminalIds(alarmIds);
        result.setTotalWorkTimeMap(totalWorkTimeMap);
        result.setTerminalAlarmsList(terminalAlarms);

        Map<Integer, Float> flowMap = dataAnalysisService.getTotalFlowMap();
        if (terminalIdSet != null) {
            Map<Integer, Float> newFlowMap = new HashMap<>();
            for (int terminalId : terminalIdSet) {
                Float flow = flowMap.get(terminalId);
                if (flow != null) {
                    newFlowMap.put(terminalId, flow);
                }
            }
            result.setTotalFlowMap(newFlowMap);
        } else {
            result.setTotalFlowMap(flowMap);
        }


        return result;
    }

}
