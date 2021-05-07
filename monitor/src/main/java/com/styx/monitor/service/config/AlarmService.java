package com.styx.monitor.service.config;

import com.styx.monitor.core.MonitorServiceSupport;
import com.styx.monitor.mapper.config.ConfigAlarmMapper;
import com.styx.monitor.model.config.ConfigAlarm;
import com.styx.monitor.service.config.cache.SimpleAlarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmService extends MonitorServiceSupport<ConfigAlarm> {

    @Autowired
    private ConfigAlarmMapper configAlarmMapper;


    public List<SimpleAlarm> findEnableSimpleAlarm() {
        return configAlarmMapper.findEnableSimpleAlarm();
    }
}
