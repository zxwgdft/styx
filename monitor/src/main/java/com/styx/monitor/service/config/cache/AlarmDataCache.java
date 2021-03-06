package com.styx.monitor.service.config.cache;

import com.styx.monitor.model.config.ConfigAlarm;
import com.styx.monitor.service.config.AlarmService;
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
    public AlarmContainer loadData(long version) {
        List<SimpleAlarm> alarms = alarmService.findEnableSimpleAlarm();
        return new AlarmContainer(alarms);
    }
}
