package com.styx.data.core.terminal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/4/20
 */
public class AlarmContainer {

    private long version;
    private List<Alarm> alarmList;
    private Map<Integer, Alarm> alarmMap;

    public AlarmContainer(long version, List<Alarm> alarmList) {
        this.version = version;

        if (alarmList == null || alarmList.size() == 0) {
            this.alarmList = Collections.emptyList();
            this.alarmMap = Collections.emptyMap();
        } else {
            this.alarmList = Collections.unmodifiableList(alarmList);
            this.alarmMap = new HashMap<>((int) (alarmList.size() / .75f) + 1);
            for (Alarm alarm : alarmList) {
                alarmMap.put(alarm.getId(), alarm);
            }
        }

    }

    public Alarm getAlarm(int id) {
        return alarmMap.get(id);
    }

    public long getVersion() {
        return version;
    }

    public List<Alarm> getAlarmList() {
        return alarmList;
    }
}
