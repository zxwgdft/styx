package com.styx.data.service;

import com.styx.common.api.R;
import com.styx.data.model.TerminalAlarm;
import com.styx.data.model.TerminalAlarmHistory;
import com.styx.data.service.dto.VersionConfig;
import com.styx.data.service.dto.VersionUpdate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author TontoZhou
 * @since 2020/11/3
 */
@FeignClient(name = "msms-monitor")
public interface InternalMonitorService {

    @PostMapping("/internal/config/update")
    VersionConfig getVersionConfig(@RequestBody VersionUpdate versionUpdate);

    @PostMapping("/internal/alarm/history")
    R uploadAlarmHistory(@RequestBody TerminalAlarmHistory alarmHistory);

    @PostMapping("/internal/alarm/trigger")
    R triggerAlarm(@RequestBody TerminalAlarm alarm);

    @PostMapping("/internal/alarm/closed")
    R closeAlarm(@RequestBody TerminalAlarmHistory alarmHistory);

    @PostMapping("/internal/alarm/untreated")
    R untreatedAlarm(@RequestBody TerminalAlarmHistory alarmHistory);

}
