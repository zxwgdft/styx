package com.styx.data.web;

import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalManager;
import com.styx.data.service.TerminalDataService;
import com.styx.data.service.dto.Data4Upload;
import com.styx.data.service.dto.DataRecord;
import com.styx.data.web.dto.TerminalDataQuery;
import com.styx.data.web.vo.TerminalAlarms;
import com.styx.data.web.vo.TerminalRealtime;
import com.styx.data.web.vo.TerminalSimpleRealtime;
import com.paladin.framework.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/11/16
 */
@Api(tags = "终端实时数据接口")
@RestController
@RequestMapping("/terminal/data")
public class TerminalDataController {

    @Autowired
    private TerminalManager terminalManager;
    @Autowired
    private TerminalDataService terminalDataService;

    @ApiOperation("获取终端简要实时数据")
    @GetMapping("/realtime/simple")
    public List<TerminalSimpleRealtime> getTerminalSimpleDataRealtime(@RequestParam(required = false) String terminalIds) {
        if (terminalIds != null && terminalIds.length() > 0) {
            String[] ids = terminalIds.split(",");
            List<TerminalSimpleRealtime> result = new ArrayList<>(ids.length);
            for (String id : ids) {
                Integer terminalId = Integer.valueOf(id);
                Terminal terminal = terminalManager.getTerminal(terminalId);
                if (terminal != null) {
                    result.add(getSimpleRealtime(terminal));
                }
            }
            return result;
        } else {
            // 如果为空则返回全部
            Collection<Terminal> terminals = terminalManager.getTerminals();
            if (terminals != null && terminals.size() > 0) {
                List<TerminalSimpleRealtime> result = new ArrayList<>(terminals.size());
                for (Terminal terminal : terminals) {
                    result.add(getSimpleRealtime(terminal));
                }
                return result;
            }
        }

        return new ArrayList<>();

    }

    @ApiOperation("获取终端实时数据")
    @GetMapping("/realtime")
    public List<TerminalRealtime> getTerminalDataRealtime(@RequestParam(required = false) String terminalIds) {
        if (terminalIds != null && terminalIds.length() > 0) {
            String[] ids = terminalIds.split(",");
            List<TerminalRealtime> result = new ArrayList<>(ids.length);
            for (String id : ids) {
                Integer terminalId = Integer.valueOf(id);
                Terminal terminal = terminalManager.getTerminal(terminalId);
                if (terminal != null) {
                    result.add(getRealtime(terminal));
                }
            }
            return result;
        } else {
            // 如果为空则返回全部
            Collection<Terminal> terminals = terminalManager.getTerminals();
            if (terminals != null && terminals.size() > 0) {
                List<TerminalRealtime> result = new ArrayList<>(terminals.size());
                for (Terminal terminal : terminals) {
                    result.add(getRealtime(terminal));
                }
                return result;
            }
        }

        return new ArrayList<>();
    }

    /**
     * 获取简要的实时数据对象
     */
    private TerminalSimpleRealtime getSimpleRealtime(Terminal terminal) {
        TerminalSimpleRealtime data = new TerminalSimpleRealtime();
        data.setId(terminal.getId());
        data.setName(terminal.getName());
        data.setStationId(terminal.getStationId());
        data.setStationName(terminal.getStationName());

        boolean isOnline = terminal.isOnline();
        data.setOnline(isOnline);

        boolean isMaintaining = terminal.isMaintaining();
        data.setMaintaining(isMaintaining);

        data.setWorkStatus(terminal.getWorkStatus());
        data.setAlarmTriggering(!terminal.getAlarmTriggeringMap().isEmpty());

        return data;
    }

    /**
     * 获取实时数据对象
     */
    private TerminalRealtime getRealtime(Terminal terminal) {
        TerminalRealtime data = new TerminalRealtime();
        data.setId(terminal.getId());
        data.setName(terminal.getName());
        data.setStationId(terminal.getStationId());
        data.setStationName(terminal.getStationName());

        boolean isOnline = terminal.isOnline();

        data.setOnline(isOnline);
        data.setLastLoginTime(terminal.getLastLoginTime());

        long lastWorkTime = terminal.getLastWorkTime();
        data.setLastWorkTime(lastWorkTime);

        if (isOnline) {
            int workCurrentMinutes = (int) ((System.currentTimeMillis() - lastWorkTime) / 60000);
            data.setWorkCurrentTime(workCurrentMinutes);
        } else {
            data.setWorkCurrentTime(0);
        }

        int workTotalMinutes = (int) (terminal.getWorkTotalTime() / 60000);
        data.setWorkTotalTime(workTotalMinutes);

        boolean isMaintaining = terminal.isMaintaining();
        data.setMaintaining(isMaintaining);

        data.setWorkStatus(terminal.getWorkStatus());
        data.setDataUpdateTime(terminal.getDataUpdateTime());
        data.setVariableValues((isOnline && !isMaintaining) ? terminal.getVariableValueMap() : null);
        data.setAlarmTriggering(!terminal.getAlarmTriggeringMap().isEmpty());

        return data;
    }

    @ApiOperation("获取所有终端报警数据")
    @GetMapping("/alarm")
    public List<TerminalAlarms> getAllTriggeringAlarms() {
        List<TerminalAlarms> terminalAlarms = new ArrayList<>();
        Collection<Terminal> terminals = terminalManager.getTerminals();
        if (terminals != null) {
            for (Terminal terminal : terminals) {
                Map<Integer, Terminal.AlarmStatus> map = terminal.getAlarmTriggeringMap();
                if (map.size() > 0) {
                    terminalAlarms.add(new TerminalAlarms(terminal.getId(), terminal.getName(),
                            terminal.getStationId(), terminal.getStationName(), map.values()));
                }
            }
        }
        return terminalAlarms;
    }

    @ApiOperation("获取终端报警数据")
    @GetMapping("/alarm/terminal")
    public TerminalAlarms getTerminalTriggeringAlarms(@RequestParam int terminalId) {
        Collection<Terminal> terminals = terminalManager.getTerminals();
        if (terminals != null) {
            for (Terminal terminal : terminals) {
                if (terminal.getId() == terminalId) {
                    Map<Integer, Terminal.AlarmStatus> map = terminal.getAlarmTriggeringMap();
                    return new TerminalAlarms(terminal.getId(), terminal.getName(),
                            terminal.getStationId(), terminal.getStationName(), map.size() > 0 ? map.values() : null);
                }
            }
        }
        return null;
    }

    @ApiOperation("获取终端历史数据")
    @PostMapping("/history")
    public List<DataRecord> getTerminalHistory(@RequestBody TerminalDataQuery query) {
        return terminalDataService.findTerminalData(query.getTerminalId(), query.getStartDate(), query.getEndDate(), query.getVariableIds());
    }

    @ApiOperation("删除测试终端数据")
    @GetMapping("/delete/test")
    public R deleteTestTerminalData(@RequestParam String terminalIds) {
        if (terminalIds != null && terminalIds.length() > 0) {
            List<Integer> tids = new ArrayList<>();
            for (String idStr : terminalIds.split(",")) {
                tids.add(Integer.valueOf(idStr));
            }
            terminalDataService.deleteTestData(tids);
        }
        return R.success();
    }

    @ApiOperation("子节点上传数据")
    @PostMapping("/upload")
    public R getUploadData(@RequestBody List<Data4Upload> data) {
        terminalDataService.receiveUploadData(data);
        return R.success();
    }


}
