package com.styx.monitor.service.config.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/3/30
 */
public class AlarmContainer {

    private Map<Integer, SimpleAlarm> alarmMap;

    public AlarmContainer(List<SimpleAlarm> alarms) {
        if (alarms == null) {
            alarmMap = new HashMap<>();
        } else {
            alarmMap = new HashMap<>((int) (alarms.size() / 0.75 + 1));
            for (SimpleAlarm alarm : alarms) {
                alarmMap.put(alarm.getId(), alarm);
            }
        }
    }

    public SimpleAlarm getAlarm(int alarmId) {
        return alarmMap.get(alarmId);
    }

    public String getAlarmName(int alarmId) {
        SimpleAlarm alarm = alarmMap.get(alarmId);
        return alarm == null ? null : alarm.getName();
    }
}
