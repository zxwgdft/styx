package com.paladin.monitor.service.config.cache;

import com.paladin.monitor.model.config.ConfigAlarm;
import com.paladin.monitor.service.config.AlarmService;
import com.styx.common.cache.DataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/3/30
 */
@Component
public class AlarmDataCache implements DataCache<AlarmContainer> {

    @Autowired
    private AlarmService alarmService;

    public String getId() {
        return "ALARM_CACHE";
    }

    @Override
    public AlarmContainer loadData() {
        List<ConfigAlarm> alarms = alarmService.findList();
        return new AlarmContainer(alarms);
    }
}
