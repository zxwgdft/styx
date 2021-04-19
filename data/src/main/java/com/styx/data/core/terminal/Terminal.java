package com.styx.data.core.terminal;

import com.googlecode.aviator.AviatorEvaluator;
import com.styx.data.service.dto.CTerminal;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 终端对象
 *
 * @author TontoZhou
 * @since 2020/10/26
 */
@Slf4j
@Getter
public class Terminal {
    // 终端ID
    private int id;
    // 终端UID
    private String uid;
    // 是否在线
    private boolean isOnline;
    // 最近登录时间
    private long lastLoginTime = -1;
    // 最近工作时间
    private long lastWorkTime = -1;
    // 工作累计时间(分钟)
    private long workTotalTime;
    // 终端变量
    private Set<Integer> variableIds;
    // 终端报警
    private Set<Integer> alarmIds;


    // 最近一次接收数据时间
    private long dataUpdateTime;
    // 最近一次接收数据
    private Map<Integer, Float> variableValueMap;
    // 报警状态
    private Map<Integer, AlarmStatus> alarmTriggeringMap;

    // 同步锁
    @Getter(AccessLevel.NONE)
    private final Object lock = new Object();


    // 终端报警处理器
    @Getter(AccessLevel.NONE)
    private TerminalAlarmHandler terminalAlarmHandler;

    @Getter(AccessLevel.NONE)
    private TerminalManager terminalManager;

    public Terminal(CTerminal cTerminal, Terminal oldTerminal, TerminalAlarmHandler terminalAlarmHandler, TerminalManager terminalManager) {
        this.id = cTerminal.getId();
        this.uid = cTerminal.getUid();
        this.variableIds = string2set(cTerminal.getVarIds());
        this.alarmIds = string2set(cTerminal.getAlarmIds());
        this.terminalAlarmHandler = terminalAlarmHandler;
        this.terminalManager = terminalManager;

        synchronized (oldTerminal.lock) {
            this.isOnline = oldTerminal.isOnline;
            this.lastLoginTime = oldTerminal.lastLoginTime;
            this.lastWorkTime = oldTerminal.lastWorkTime;
            this.workTotalTime = oldTerminal.workTotalTime;
            this.dataUpdateTime = oldTerminal.dataUpdateTime;
            this.variableValueMap = oldTerminal.variableValueMap;
            this.alarmTriggeringMap = new HashMap<>();

            Map<Integer, AlarmStatus> oldAlarmMap = oldTerminal.alarmTriggeringMap;
            if (oldAlarmMap != null) {
                Collection<Alarm> alarms = getAlarms();
                if (alarms != null) {
                    for (Alarm alarm : alarms) {
                        Integer aid = alarm.getId();
                        AlarmStatus alarmStatus = oldAlarmMap.remove(aid);
                        if (alarmStatus != null) {
                            this.alarmTriggeringMap.put(aid, alarmStatus);
                        }
                    }
                }

                /*
                 * 不再属于该终端但之前已经存在的报警，需要关闭
                 */
                for (Map.Entry<Integer, AlarmStatus> entry : oldAlarmMap.entrySet()) {
                    int aid = entry.getKey();
                    log.info("报警[terminalID:{},alarmID:{}]被忽略", id, aid);
                }
            }
        }
    }

    public Terminal(CTerminal cTerminal, Terminal oldTerminal, TerminalAlarmHandler terminalAlarmHandler, TerminalManager terminalManager) {
        this.id = cTerminal.getId();
        this.uid = cTerminal.getUid();
        this.variableIds = string2set(cTerminal.getVarIds());
        this.alarmIds = string2set(cTerminal.getAlarmIds());
        this.terminalAlarmHandler = terminalAlarmHandler;
        this.terminalManager = terminalManager;

        synchronized (oldTerminal.lock) {
            this.isOnline = oldTerminal.isOnline;
            this.lastLoginTime = oldTerminal.lastLoginTime;
            this.lastWorkTime = oldTerminal.lastWorkTime;
            this.workTotalTime = oldTerminal.workTotalTime;
            this.dataUpdateTime = oldTerminal.dataUpdateTime;
            this.variableValueMap = oldTerminal.variableValueMap;
            this.alarmTriggeringMap = new HashMap<>();

            Map<Integer, AlarmStatus> oldAlarmMap = oldTerminal.alarmTriggeringMap;
            if (oldAlarmMap != null) {
                Collection<Alarm> alarms = getAlarms();
                if (alarms != null) {
                    for (Alarm alarm : alarms) {
                        Integer aid = alarm.getId();
                        AlarmStatus alarmStatus = oldAlarmMap.remove(aid);
                        if (alarmStatus != null) {
                            this.alarmTriggeringMap.put(aid, alarmStatus);
                        }
                    }
                }

                /*
                 * 不再属于该终端但之前已经存在的报警，需要关闭
                 */
                for (Map.Entry<Integer, AlarmStatus> entry : oldAlarmMap.entrySet()) {
                    int aid = entry.getKey();
                    log.info("报警[terminalID:{},alarmID:{}]被忽略", id, aid);
                }
            }
        }
    }

    private Set<Integer> string2set(String ids) {
        Set<Integer> set = new HashSet<>();
        if (ids != null && ids.length() > 0) {
            String[] idArr = ids.split(",");
            for (String id : idArr) {
                set.add(Integer.valueOf(id));
            }
        }
        return set;
    }


    /**
     * 获取累计工作时间
     */
    public long getWorkTotalTime() {
        synchronized (lock) {
            if (isOnline) {
                return workTotalTime + System.currentTimeMillis() - lastWorkTime;
            } else {
                return workTotalTime;
            }
        }
    }


    /**
     * 终端离线
     */
    public void offline() {
        synchronized (lock) {
            if (isOnline) {
                if (lastWorkTime > 0) {
                    workTotalTime += System.currentTimeMillis() - lastWorkTime;
                }
                isOnline = false;
            }
        }
    }

    // 1分钟
    @Getter(AccessLevel.NONE)
    private long timeOut4Online = 60 * 1000;

    /**
     * 检查是否在线（超时判断）
     */
    public void checkOnline() {
        synchronized (lock) {
            if (isOnline) {
                long d = System.currentTimeMillis() - dataUpdateTime;
                if (d > timeOut4Online) {
                    offline();
                }
            }
        }
    }


    /**
     * 获取终端所有变量
     */
    public Collection<Variable> getVariables() {
        Map<Integer, Variable> variableMap = terminalManager.getVariableMap();
        List<Variable> variables = new ArrayList<>(this.variableIds.size());
        for (Integer varId : this.variableIds) {
            Variable variable = variableMap.get(varId);
            if (variable != null) {
                variables.add(variable);
            }
        }
        return variables;
    }

    /**
     * 获取需要持久化的变量
     */
    public Collection<Variable> getPersistedVariables() {
        Map<Integer, Variable> variableMap = terminalManager.getVariableMap();
        List<Variable> variables = new ArrayList<>(this.variableIds.size());
        for (Integer varId : this.variableIds) {
            Variable variable = variableMap.get(varId);
            if (variable != null && variable.isPersisted()) {
                variables.add(variable);
            }
        }
        return variables;
    }

    /**
     * 获取所有报警配置
     */
    public Collection<Alarm> getAlarms() {
        Map<Integer, Alarm> alarmMap = terminalManager.getAlarmMap();
        if (this.alarmIds.size() > 0) {
            List<Alarm> alarms = new ArrayList<>(alarmMap.size());
            for (Alarm alarm : alarmMap.values()) {
                if (this.alarmIds.contains(alarm.getId())) {
                    alarms.add(alarm);
                }
            }
            return alarms;
        } else {
            return Collections.emptyList();
        }
    }

    // 报警超时间隔 24小时
    @Getter(AccessLevel.NONE)
    private long timeOut4Alarm = 48 * 60 * 60 * 1000;

    /**
     * 设置数据
     */
    public void setData(int workStatus, Map<Integer, Float> variableValueMap, boolean checkAlarm) {
        synchronized (lock) {
            long now = System.currentTimeMillis();
            // 上线
            if (!isOnline) {
                lastLoginTime = lastWorkTime = System.currentTimeMillis();
                isOnline = true;
                this.workStatus = workStatus;
            } else {
                // 如果已经上线，但是工作状态改变
                if (workStatus != this.workStatus) {
                    if (lastWorkTime > 0) {
                        workTotalTime += now - lastWorkTime;
                    }
                    lastWorkTime = now;
                    this.workStatus = workStatus;
                }
            }

            // 更新变量数据
            this.variableValueMap = variableValueMap;

            if (checkAlarm) {
                // 分析报警
                Collection<Alarm> alarms = getAlarms();
                Set<Integer> triggerAlarmIds = new HashSet<>();
                for (Alarm alarm : alarms) {
                    Map<String, Float> valueMap = new HashMap<>();
                    for (Integer id : alarm.getVariableIds()) {
                        Float value = variableValueMap.get(id);
                        if (value != null) {
                            valueMap.put("value" + id, value);
                        }
                    }
                    String expression = alarm.getFormulaTemplate().createExpression(valueMap);
                    if (expression == null) {
                        continue;
                    }
                    boolean isAlarm = (boolean) AviatorEvaluator.execute(expression);
                    if (isAlarm) {
                        triggerAlarmIds.add(alarm.getId());
                    }
                }

                // 如果之前没有的报警判断为新出现报警
                for (Integer aid : triggerAlarmIds) {
                    if (!alarmTriggeringMap.containsKey(aid)) {
                        try {
                            terminalAlarmHandler.alarmTriggerHandle(id, aid, now);
                            alarmTriggeringMap.put(aid, new AlarmStatus(aid, now, now));
                        } catch (Exception e) {
                            log.error("报警[terminalID:" + id + ",alarmID:" + aid + "]新增处理调用异常", e);
                        }
                    }
                }

                // 判断并关闭报警
                Iterator<Map.Entry<Integer, AlarmStatus>> alarmIterator = alarmTriggeringMap.entrySet().iterator();
                while (alarmIterator.hasNext()) {
                    Map.Entry<Integer, AlarmStatus> entry = alarmIterator.next();
                    int aid = entry.getKey();
                    if (!triggerAlarmIds.contains(aid)) {
                        AlarmStatus alarmStatus = entry.getValue();
                        if (alarmStatus != null) {
                            try {
                                terminalAlarmHandler.alarmClosedHandle(id, aid, alarmStatus.startTime);
                            } catch (Exception e) {
                                log.error("报警[terminalID:" + id + ",alarmID:" + aid + "]关闭处理调用异常", e);
                            }
                        }
                        alarmIterator.remove();
                    }
                }
            }


            this.dataUpdateTime = System.currentTimeMillis();

            // 分发，需要注意是否在维护状态下也分发事件
            terminalManager.dispatchTerminalDataChangeEvent(this);
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlarmStatus {
        // 报警ID
        private int id;
        // 第一次触发时间
        private long startTime;

    }
}
