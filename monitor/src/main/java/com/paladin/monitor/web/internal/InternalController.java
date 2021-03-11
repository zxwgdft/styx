package com.paladin.monitor.web.internal;

import com.paladin.framework.common.R;
import com.paladin.monitor.core.config.CAlarmContainer;
import com.paladin.monitor.core.config.COthersContainer;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.config.CVariableContainer;
import com.paladin.monitor.model.data.DataAnalysisDay;
import com.paladin.monitor.model.data.DataAnalysisHour;
import com.paladin.monitor.service.data.DataAnalysisService;
import com.paladin.monitor.service.config.AlarmHistoryService;
import com.paladin.monitor.service.config.StationService;
import com.paladin.monitor.service.config.dto.TerminalAlarmHistory;
import com.paladin.monitor.web.internal.dto.VersionConfig;
import com.paladin.monitor.web.internal.dto.VersionUpdate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/10/30
 */
@Api(tags = "内部参数配置接口")
@RestController
@RequestMapping("/internal/")
public class InternalController {

    @Autowired
    private CVariableContainer variableContainer;

    @Autowired
    private CTerminalContainer terminalContainer;

    @Autowired
    private CAlarmContainer alarmContainer;

    @Autowired
    private COthersContainer othersContainer;

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Autowired
    private AlarmHistoryService alarmHistoryService;

    @Autowired
    private StationService stationService;

    @ApiOperation("配置版本更新")
    @PostMapping("/config/update")
    public VersionConfig getVariable(@RequestBody VersionUpdate versionUpdate) {
        VersionConfig versionConfig = new VersionConfig();

        if (variableContainer.getVersion() > versionUpdate.getVariableVersion()) {
            versionConfig.setVariableVersion(variableContainer.getVersion());
            versionConfig.setVariables(variableContainer.getCVariables());
        } else {
            versionConfig.setVariableVersion(versionUpdate.getVariableVersion());
        }

        if (alarmContainer.getVersion() > versionUpdate.getAlarmVersion()) {
            versionConfig.setAlarmVersion(alarmContainer.getVersion());
            versionConfig.setAlarms(alarmContainer.getEnabledAlarms());
        } else {
            versionConfig.setAlarmVersion(versionUpdate.getAlarmVersion());
        }

        if (othersContainer.getVersion() > versionUpdate.getOthersVersion()) {
            versionConfig.setOthersVersion(othersContainer.getVersion());
            versionConfig.setOthers(othersContainer.getOthers());
        } else {
            versionConfig.setOthersVersion(versionUpdate.getOthersVersion());
        }

        if (terminalContainer.getVersion() > versionUpdate.getTerminalVersion()) {
            versionConfig.setTerminalVersion(terminalContainer.getVersion());
            versionConfig.setTerminals(terminalContainer.getTerminalOfServerNode(versionUpdate.getServerNode()));
        } else {
            versionConfig.setTerminalVersion(versionUpdate.getTerminalVersion());
        }

        versionConfig.setParentNodeCode(terminalContainer.getParentServerNodeCode(versionUpdate.getServerNode()));

        return versionConfig;
    }


    @ApiOperation("报警记录上传")
    @PostMapping("/alarm/history")
    public R uploadAlarmHistory(@RequestBody TerminalAlarmHistory alarmHistory) {
        alarmHistoryService.saveAlarmHistory(alarmHistory);
        return R.success();
    }

    @ApiOperation("报警触发")
    @PostMapping("/alarm/trigger")
    public R triggerAlarm(@RequestBody TerminalAlarmHistory alarmHistory) {
        alarmHistoryService.alarmTriggerHandle(alarmHistory);
        return R.success();
    }

    @ApiOperation("报警关闭")
    @PostMapping("/alarm/closed")
    public R closeAlarm(@RequestBody TerminalAlarmHistory alarmHistory) {
        alarmHistoryService.alarmClosedHandle(alarmHistory);
        return R.success();
    }

    @ApiOperation("报警未处理")
    @PostMapping("/alarm/untreated")
    public R untreatedAlarm(@RequestBody TerminalAlarmHistory alarmHistory) {
        alarmHistoryService.alarmUntreatedHandle(alarmHistory);
        return R.success();
    }

    @ApiOperation("日报统计数据上传")
    @PostMapping("/data/analysis/hour/upload")
    public R uploadDataAnalysisHour(@RequestBody List<DataAnalysisHour> data) {
        dataAnalysisService.uploadDataAnalysisHour(data);
        return R.success();
    }


    @ApiOperation("月报统计数据上传")
    @PostMapping("/data/analysis/day/upload")
    public R uploadDataAnalysisDay(@RequestBody List<DataAnalysisDay> data) {
        dataAnalysisService.uploadDataAnalysisDay(data);
        return R.success();
    }

    @ApiOperation("更新站点拼音名称")
    @GetMapping("/update/station/pinyin")
    public R updateStationPinyinName(){
        stationService.updatePinyinName();
        return R.success();
    }

}
