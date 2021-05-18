package com.styx.monitor.service.config;

import com.styx.monitor.core.MonitorServiceSupport;
import com.styx.monitor.mapper.config.ConfigAlarmMapper;
import com.styx.monitor.model.config.ConfigAlarm;
import com.styx.monitor.service.config.cache.SimpleAlarm;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmService extends MonitorServiceSupport<ConfigAlarm, ConfigAlarmMapper> {

    public List<SimpleAlarm> findEnableSimpleAlarm() {
        return getSqlMapper().findEnableSimpleAlarm();
    }
}
