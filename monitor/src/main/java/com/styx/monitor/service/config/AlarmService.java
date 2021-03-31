package com.styx.monitor.service.config;

import com.styx.common.service.ServiceSupport;
import com.styx.monitor.mapper.config.ConfigAlarmMapper;
import com.styx.monitor.model.config.ConfigAlarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmService extends ServiceSupport<ConfigAlarm> {

    @Autowired
    private ConfigAlarmMapper configAlarmMapper;


}