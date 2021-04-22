package com.styx.data.core.terminal;

import com.googlecode.aviator.AviatorEvaluator;
import com.styx.common.utils.StringUtil;
import com.styx.data.model.TerminalInfo;
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
    public Terminal(CTerminal cTerminal, TerminalInfo terminalInfo, List<AlarmStatus> alarmTriggeringList, TerminalManager terminalManager) {
        this.id = cTerminal.getId();
        this.uid = cTerminal.getUid();
        this.variableIds = StringUtil.stringToIntegerSet(cTerminal.getVarIds());
        this.alarmIds = StringUtil.stringToIntegerSet(cTerminal.getAlarmIds());
        this.terminalManager = terminalManager;

        setAlarmTriggering(alarmTriggeringList);

        if (terminalInfo != null) {
            this.workTotalTime = terminalInfo.getWorkTotalTime() * 60000L;
            this.dataUpdateTime = terminalInfo.getUpdateTime().getTime();
            Date date = terminalInfo.getLastLoginTime();
            if (date != null) {
                this.lastLoginTime = date.getTime();
            }
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
            isOnline = false;
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
                    isOnline = false;
                }
            }
        }
    }

    private long variableVersion = -1;
    private List<Variable> variables;

    /**
     * 获取终端所有变量
     */
    public List<Variable> getVariables() {
        VariableContainer variableContainer = terminalManager.getVariableContainer();
        if (variableContainer == null) {
            return Collections.emptyList();
        }

        long version = variableContainer.getVersion();
        if (version > variableVersion) {
            synchronized (lock) {
                if (version > variableVersion) {
                    List<Variable> variables = new ArrayList<>(this.variableIds.size());
                    for (Integer varId : this.variableIds) {
                        Variable variable = variableContainer.getVariable(varId);
                        if (variable != null) {
                            variables.add(variable);
                        }
                    }
                    this.variables = variables;
                    this.variableVersion = version;
                }
            }
        }
        return this.variables;
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
    public void setData(Map<Integer, Float> variableValueMap) {
        synchronized (lock) {
            long now = System.currentTimeMillis();
            // 上线
            if (!isOnline) {
                lastLoginTime = lastWorkTime = now;
                isOnline = true;
            } else {
                if (lastWorkTime > 0) {
                    workTotalTime += now - lastWorkTime;
                }
                lastWorkTime = now;
            }

            // 更新变量数据
            this.variableValueMap = variableValueMap;

            // 分析报警
            Collection<Alarm> alarms = getAlarms();
            Set<Integer> triggerAlarmIds = new HashSet<>();
            for (Alarm alarm : alarms) {
                Map<String, Float> valueMap = new HashMap<>();
                for (Integer id : alarm.getVariableIds()) {
                    Float value = variableValueMap.get(id);
                    if (value != null) {
                        valueMap.put("$" + id, value);
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
                if (!alarmTriggeringMap1.containsKey(aid)) {
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


            dataUpdateTime = now;
            terminalManager.dispatchTerminalDataChangeEvent(this);
        }
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
