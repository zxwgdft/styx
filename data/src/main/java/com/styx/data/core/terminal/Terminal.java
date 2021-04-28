package com.styx.data.core.terminal;

import com.googlecode.aviator.AviatorEvaluator;
import com.styx.common.utils.StringUtil;
import com.styx.data.service.dto.CTerminal;
import lombok.AccessLevel;
import lombok.Getter;
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
    // 最近一次接收数据时间
    private long dataUpdateTime;
    // 工作累计时间(分钟)
    private long workTotalTime;


    // 终端变量
    private Set<Integer> variableIds;
    // 终端报警
    private Set<Integer> alarmIds;

    /*
     * 变量和报警cache用
     */
    private long variableVersion = -1;
    private List<Variable> variables;
    private List<Variable> alarmVariables;
    private long alarmVersion = -1;
    private List<Alarm> alarms;


    // 最近一次接收数据
    private Map<Integer, Float> variableValueMap;


    // 公式报警map
    private Map<Integer, AlarmStatus> alarmTriggeringMap1;
    // 变量报警map
    private Map<Integer, AlarmStatus> alarmTriggeringMap2;
    // 所有报警集合
    private List<AlarmStatus> alarmTriggeringList;


    // 同步锁
    @Getter(AccessLevel.NONE)
    private final Object lock = new Object();

    @Getter(AccessLevel.NONE)
    private TerminalManager terminalManager;

    /**
     * 用于新老对象替换
     *
     * @param cTerminal       总的配置
     * @param oldTerminal     旧终端对象
     * @param terminalManager 终端管理器
     */
    public Terminal(CTerminal cTerminal, Terminal oldTerminal, TerminalManager terminalManager) {
        this.id = cTerminal.getId();
        this.uid = cTerminal.getUid();
        this.variableIds = StringUtil.stringToIntegerSet(cTerminal.getVarIds());
        this.alarmIds = StringUtil.stringToIntegerSet(cTerminal.getAlarmIds());
        this.terminalManager = terminalManager;

        synchronized (oldTerminal.lock) {
            this.isOnline = oldTerminal.isOnline;
            this.lastLoginTime = oldTerminal.lastLoginTime;
            this.workTotalTime = oldTerminal.workTotalTime;
            this.dataUpdateTime = oldTerminal.dataUpdateTime;
            this.variableValueMap = oldTerminal.variableValueMap;

            setAlarmTriggering(oldTerminal.alarmTriggeringList);
        }
    }

    /**
     * 第一次创建终端对象
     *
     * @param cTerminal           总的配置
     * @param terminalInfo        终端信息（持久化的历史数据）
     * @param alarmTriggeringList 终端报警信息（持久化的历史数据）
     * @param terminalManager     终端管理器
     */
    public Terminal(CTerminal cTerminal, TerminalPersistedInfo terminalInfo, List<AlarmStatus> alarmTriggeringList, TerminalManager terminalManager) {
        this.id = cTerminal.getId();
        this.uid = cTerminal.getUid();
        this.variableIds = StringUtil.stringToIntegerSet(cTerminal.getVarIds());
        this.alarmIds = StringUtil.stringToIntegerSet(cTerminal.getAlarmIds());
        this.terminalManager = terminalManager;

        setAlarmTriggering(alarmTriggeringList);

        if (terminalInfo != null) {
            this.workTotalTime = terminalInfo.getWorkTotalTime() * 60000L;
            this.dataUpdateTime = terminalInfo.getDataUpdateTime();
            this.lastLoginTime = terminalInfo.getLastLoginTime();
        }
    }

    private void setAlarmTriggering(List<AlarmStatus> alarmTriggeringList) {
        this.alarmTriggeringMap1 = new HashMap<>();
        this.alarmTriggeringMap2 = new HashMap<>();

        if (alarmTriggeringList == null || alarmTriggeringList.size() == 0) {
            this.alarmTriggeringList = new ArrayList<>();
        } else {
            this.alarmTriggeringList = new ArrayList<>(alarmTriggeringList.size());
            for (AlarmStatus alarmStatus : alarmTriggeringList) {
                int type = alarmStatus.getType();
                int aid = alarmStatus.getId();
                if (type == AlarmStatus.TYPE_FORMULA) {
                    if (alarmIds.contains(aid)) {
                        this.alarmTriggeringMap1.put(aid, alarmStatus);
                        this.alarmTriggeringList.add(alarmStatus);
                    } else {
                        log.info("报警[terminalID:{},alarmID:{}]被忽略", id, aid);
                    }
                } else if (type == AlarmStatus.TYPE_VARIABLE) {
                    if (variableIds.contains(aid)) {
                        this.alarmTriggeringMap2.put(aid, alarmStatus);
                        this.alarmTriggeringList.add(alarmStatus);
                    } else {
                        log.info("报警[terminalID:{},variableID:{}]被忽略", id, aid);
                    }
                }
            }
        }
    }


    /**
     * 终端离线
     */
    public void offline() {
        synchronized (lock) {
            if (isOnline) {
                isOnline = false;
                terminalManager.dispatchTerminalOfflineEvent(this);
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
        long d = System.currentTimeMillis() - dataUpdateTime;
        if (isOnline && d > timeOut4Online) {
            synchronized (lock) {
                if (isOnline) {
                    isOnline = false;
                    terminalManager.dispatchTerminalOfflineEvent(this);
                }
            }
        }
    }


    /**
     * 获取终端所有变量
     */
    public List<Variable> getVariables() {
        readVariable();
        return this.variables;
    }


    /**
     * 获取终端所有报警类型变量
     */
    public List<Variable> getAlarmVariables() {
        readVariable();
        return this.alarmVariables;
    }

    private void readVariable() {
        VariableContainer variableContainer = terminalManager.getVariableContainer();
        if (variableContainer == null) {
            synchronized (lock) {
                variableContainer = terminalManager.getVariableContainer();
                if (variableContainer == null) {
                    this.variables = Collections.emptyList();
                    this.alarmVariables = Collections.emptyList();
                    return;
                }
            }
        }

        long version = variableContainer.getVersion();
        if (version > variableVersion) {
            synchronized (lock) {
                if (version > variableVersion) {
                    int size = this.variableIds.size();
                    List<Variable> variables = new ArrayList<>(size);
                    List<Variable> alarmVariables = new ArrayList<>(size / 2 + 1);
                    for (Integer varId : this.variableIds) {
                        Variable variable = variableContainer.getVariable(varId);
                        if (variable != null) {
                            variables.add(variable);
                            if (variable.isAlarm()) {
                                alarmVariables.add(variable);
                            }
                        }
                    }
                    this.variables = variables;
                    this.alarmVariables = alarmVariables;
                    variableVersion = version;
                }
            }
        }
    }

    /**
     * 获取所有报警配置
     */
    public List<Alarm> getAlarms() {
        AlarmContainer alarmContainer = terminalManager.getAlarmContainer();
        if (alarmContainer == null) {
            synchronized (lock) {
                alarmContainer = terminalManager.getAlarmContainer();
                if (alarmContainer == null) {
                    this.alarms = Collections.emptyList();
                    return this.alarms;
                }
            }
        }

        long version = alarmContainer.getVersion();
        if (version > alarmVersion) {
            synchronized (lock) {
                if (version > alarmVersion) {
                    List<Alarm> alarms = new ArrayList<>(this.alarmIds.size());
                    for (Integer aid : this.alarmIds) {
                        Alarm alarm = alarmContainer.getAlarm(aid);
                        if (alarm != null) {
                            alarms.add(alarm);
                        }
                    }
                    this.alarms = alarms;
                    this.alarmVersion = version;
                }
            }
        }

        return alarms;
    }


    /**
     * 设置数据
     */
    public void setData(Map<Integer, Float> variableValueMap) {
        synchronized (lock) {
            long now = System.currentTimeMillis();
            // 上线
            if (!isOnline) {
                dataUpdateTime = now;
                isOnline = true;
                lastLoginTime = now;
                terminalManager.dispatchTerminalOnlineEvent(this);
            } else {
                workTotalTime += now - dataUpdateTime;
                dataUpdateTime = now;
            }


            // 更新变量数据
            this.variableValueMap = variableValueMap;

            // 分析报警
            try {
                analyzeAlarm();
            } catch (Exception e) {

            }

            terminalManager.dispatchTerminalDataChangeEvent(this);
        }
    }


    private void analyzeAlarm() {

        long now = System.currentTimeMillis();

        // 分析报警公式
        List<Alarm> alarms = getAlarms();

        // 取一般容量初始化
        HashMap<Integer, AlarmStatus> triggerAlarms1 = new HashMap<>(Math.max((int) (alarms.size() / .75f / 2) + 1, 16));
        for (Alarm alarm : alarms) {
            Map<String, Float> valueMap = new HashMap<>();
            for (Integer id : alarm.getVariableIds()) {
                Float value = variableValueMap.get(id);
                if (value != null) {
                    valueMap.put("$" + id, value);
                }
            }
            // 创建java支持的表达式
            String expression = alarm.getFormulaTemplate().createExpression(valueMap);
            if (expression == null) {
                continue;
            }
            // 执行表达式
            boolean isAlarm = (boolean) AviatorEvaluator.execute(expression);
            if (isAlarm) {
                triggerAlarms1.put(alarm.getId(), new AlarmStatus(alarm.getId(), AlarmStatus.TYPE_FORMULA, now, -1));
            }
        }

        // 分析报警变量
        List<Variable> variables = getAlarmVariables();
        // 取一般容量初始化
        Map<Integer, AlarmStatus> triggerAlarms2 = new HashMap<>(Math.max((int) (variables.size() / .75f / 2) + 1, 16));
        for (Variable variable : variables) {
            int vid = variable.getId();
            Float value = variableValueMap.get(vid);
            if (value != null && value.intValue() == 1) {
                triggerAlarms2.put(vid, new AlarmStatus(vid, AlarmStatus.TYPE_VARIABLE, now, -1));
            }
        }


        List<AlarmStatus> newAlarms = new ArrayList<>(triggerAlarms1.size() + triggerAlarms2.size());
        List<AlarmStatus> closedAlarms = new ArrayList<>(alarmTriggeringMap1.size() + alarmTriggeringMap2.size());


        // 分析新出的报警
        for (Map.Entry<Integer, AlarmStatus> entry : triggerAlarms1.entrySet()) {
            if (!alarmTriggeringMap1.containsKey(entry.getKey())) {
                newAlarms.add(entry.getValue());
            }
        }

        for (Map.Entry<Integer, AlarmStatus> entry : triggerAlarms2.entrySet()) {
            if (!alarmTriggeringMap2.containsKey(entry.getKey())) {
                newAlarms.add(entry.getValue());
            }
        }

        // 分析关闭的报警
        for (Map.Entry<Integer, AlarmStatus> entry : alarmTriggeringMap1.entrySet()) {
            if (!triggerAlarms1.containsKey(entry.getKey())) {
                AlarmStatus alarmStatus = entry.getValue();
                alarmStatus.setCloseTime(now);
                closedAlarms.add(alarmStatus);
            }
        }

        for (Map.Entry<Integer, AlarmStatus> entry : alarmTriggeringMap2.entrySet()) {
            if (!triggerAlarms2.containsKey(entry.getKey())) {
                AlarmStatus alarmStatus = entry.getValue();
                alarmStatus.setCloseTime(now);
                closedAlarms.add(alarmStatus);
            }
        }

        List<AlarmStatus> alarmTriggeringList = new ArrayList<>(triggerAlarms1.size() + triggerAlarms2.size());
        alarmTriggeringList.addAll(triggerAlarms1.values());
        alarmTriggeringList.addAll(triggerAlarms2.values());

        this.alarmTriggeringList = alarmTriggeringList;
        this.alarmTriggeringMap1 = triggerAlarms1;
        this.alarmTriggeringMap2 = triggerAlarms2;


        terminalManager.dispatchTerminalAlarmTriggerEvent(this, newAlarms);
        terminalManager.dispatchTerminalClosedTriggerEvent(this, closedAlarms);
    }

    /**
     * 获取终端UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * 获取终端ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取累计工作时间
     */
    public long getWorkTotalTime() {
        return workTotalTime;
    }

    /**
     * 获取数据最近更新时间
     */
    public long getDataUpdateTime() {
        return dataUpdateTime;
    }

    /**
     * 获取最近一次登录
     *
     * @return
     */
    public long getLastLoginTime() {
        return lastLoginTime;
    }

}
