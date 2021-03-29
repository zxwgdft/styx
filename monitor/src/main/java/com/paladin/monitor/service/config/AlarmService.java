package com.paladin.monitor.service.config;

import com.paladin.monitor.mapper.config.ConfigAlarmMapper;
import com.paladin.monitor.model.config.ConfigAlarm;
import com.styx.common.service.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmService extends ServiceSupport<ConfigAlarm> {

    @Autowired
    private ConfigAlarmMapper configAlarmMapper;

}
