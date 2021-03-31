package com.styx.monitor.service.config.cache;

import com.styx.monitor.model.config.ConfigAlarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/3/30
 */
public class AlarmContainer {

    private Map<Integer, ConfigAlarm> alarmMap;

    public AlarmContainer(List<ConfigAlarm> alarms) {
        if (alarms == null) {
            alarmMap = new HashMap<>();
        } else {
            alarmMap = new HashMap<>((int) (alarms.size() / 0.75 + 1));
            for (ConfigAlarm alarm : alarms) {
                alarmMap.put(alarm.getId(), alarm);
            }
        }
    }

    public ConfigAlarm getAlarm(int alarmId) {
        return alarmMap.get(alarmId);
    }

    public String getAlarmName(int alarmId) {
        ConfigAlarm alarm = alarmMap.get(alarmId);
        return alarm == null ? null : alarm.getName();
    }
}
