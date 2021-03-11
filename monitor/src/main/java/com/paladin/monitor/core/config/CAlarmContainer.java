package com.paladin.monitor.core.config;

import com.paladin.monitor.service.config.AlarmService;
import com.paladin.monitor.service.config.vo.AlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author TontoZhou
 * @since 2020/11/4
 */
@Slf4j
@Component
public class CAlarmContainer extends ConfigContainer {

    @Autowired
    private AlarmService alarmService;

    private List<CAlarm> alarms;

    private Map<Integer, CAlarm> enabledAlarmMap;
    private Map<Integer, AlarmVO> alarmMap;

    @Override
    public String getId() {
        return ConfigContainer.CONTAINER_ALARM;
    }

    @Override
    public void load() {
        List<AlarmVO> list = alarmService.findAll(AlarmVO.class);
        List<CAlarm> alarms = new ArrayList<>();
        Map<Integer, AlarmVO> alarmMap = new HashMap<>();
        Map<Integer, CAlarm> enabledAlarmMap = new HashMap<>();
        if (list != null) {
            for (AlarmVO item : list) {
                if (item.getEnabled()) {
                    CAlarm alarm = new CAlarm();
                    alarm.setId(item.getId());
                    alarm.setName(item.getType());
                    alarm.setFormula(item.getLogic());
                    alarm.setVariableIds(item.getVariableList());

                    String targetString = item.getNoticeTarget();
                    if (targetString != null && targetString.length() > 0) {
                        Set<Integer> targets = new HashSet<>();
                        for (String str : targetString.split(",")) {
                            targets.add(Integer.valueOf(str));
                        }
                        alarm.setNoticeTarget(targets);
                    }

                    alarms.add(alarm);
                    enabledAlarmMap.put(item.getId(), alarm);
                }
                alarmMap.put(item.getId(), item);
            }
        }

        this.alarms = alarms;
        this.alarmMap = alarmMap;
        this.enabledAlarmMap = enabledAlarmMap;
    }


    public List<CAlarm> getEnabledAlarms() {
        return alarms;
    }

    public CAlarm getEnabledAlarm(int id) {
        return enabledAlarmMap.get(id);
    }

    public String getAlarmName(int id) {
        AlarmVO alarm = alarmMap.get(id);
        return alarm == null ? null : alarm.getType();
    }
}
