package com.styx.data.core.terminal;

import com.styx.data.service.dto.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 终端管理器的抽象实现
 * <p>
 * 1.通过版本加载配置
 * 2.通过数组序号切换达到配置的加载更换
 *
 * @author TontoZhou
 * @since 2021/4/20
 */
@Slf4j
public abstract class AbstractTerminalManager implements TerminalManager {


    private int index = 0;
    private final TerminalContainer[] terminalContainerArray = new TerminalContainer[2];
    private final VariableContainer[] variableContainerArray = new VariableContainer[2];
    private final AlarmContainer[] alarmContainerArray = new AlarmContainer[2];

    protected String nodeName;
    protected List<TerminalListener> terminalListeners;

    /**
     * 加载配置
     */
    public synchronized void loadConfig() {
        try {
            TerminalContainer terminalContainer = terminalContainerArray[index];
            VariableContainer variableContainer = variableContainerArray[index];
            AlarmContainer alarmContainer = alarmContainerArray[index];

            long curTerminalVersion = terminalContainer == null ? -1 : terminalContainer.getVersion();
            long curVariableVersion = variableContainer == null ? -1 : variableContainer.getVersion();
            long curAlarmVersion = alarmContainer == null ? -1 : alarmContainer.getVersion();

            VersionUpdate versionUpdate = new VersionUpdate(nodeName);
            versionUpdate.setAlarmVersion(curAlarmVersion);
            versionUpdate.setVariableVersion(curVariableVersion);
            versionUpdate.setTerminalVersion(curTerminalVersion);

            VersionConfig versionConfig = getVersionConfig(versionUpdate);

            int nextIndex = (index + 1) % 2;

            long vVersion = versionConfig.getVariableVersion();

            if (vVersion > curVariableVersion) {
                // 更新变量配置
                List<CVariable> cVariables = versionConfig.getVariables();
                List<Variable> variableList = cVariables == null ? null :
                        new ArrayList<>(cVariables.size());

                if (cVariables != null && cVariables.size() > 0) {
                    for (CVariable cVariable : cVariables) {
                        Variable variable = new Variable();
                        variable.setId(cVariable.getId());
                        variable.setName(cVariable.getName());
                        variable.setBytePosition(cVariable.getBytePosition());
                        variable.setBitPosition(cVariable.getBitPosition());
                        variable.setType(cVariable.getType());
                        variable.setPersisted(cVariable.isPersisted());
                        variableList.add(variable);
                    }
                }

                variableContainerArray[nextIndex] = new VariableContainer(vVersion, variableList);
                log.debug("更新变量配置，版本号：" + vVersion);
            } else {
                variableContainerArray[nextIndex] = variableContainer;
            }

            long aVersion = versionConfig.getAlarmVersion();

            if (aVersion > curAlarmVersion) {
                List<CAlarm> cAlarms = versionConfig.getAlarms();
                List<Alarm> alarmList = cAlarms == null ? null :
                        new ArrayList<>(cAlarms.size());

                if (cAlarms != null) {
                    for (CAlarm cAlarm : cAlarms) {
                        int aid = cAlarm.getId();
                        Alarm alarm = new Alarm(aid, cAlarm.getName(), cAlarm.getFormula(), cAlarm.getVariableIds());
                        alarmList.add(alarm);
                    }
                }

                alarmContainerArray[nextIndex] = new AlarmContainer(aVersion, alarmList);
                log.debug("更新报警配置，版本号：" + aVersion);
            } else {
                alarmContainerArray[nextIndex] = alarmContainer;
            }


            long tVersion = versionConfig.getTerminalVersion();

            if (tVersion > curTerminalVersion) {
                // 更新终端配置
                List<CTerminal> cTerminals = versionConfig.getTerminals();
                List<Terminal> terminalList = cTerminals == null ? null :
                        new ArrayList<>(cTerminals.size());

                if (cTerminals != null) {
                    for (CTerminal cTerminal : cTerminals) {
                        Terminal oldTerminal = getTerminal(cTerminal.getUid());
                        if (oldTerminal != null) {
                            terminalList.add(new Terminal(cTerminal, oldTerminal, this));
                        } else {
                            int tid = cTerminal.getId();
                            TerminalPersistedInfo terminalInfo = getTerminalInfo(tid);
                            List<AlarmStatus> alarmStatuses = getTerminalAlarmStatus(tid);
                            terminalList.add(new Terminal(cTerminal, terminalInfo, alarmStatuses, this));
                        }
                    }
                }

                terminalContainerArray[nextIndex] = new TerminalContainer(tVersion, terminalList);
                log.debug("更新终端配置，版本号：" + tVersion);
            } else {
                terminalContainerArray[nextIndex] = terminalContainer;
            }

            // 切换到配置组,并删除旧配置
            int old = this.index;
            this.index = nextIndex;
        } catch (Exception e) {
            log.error("尝试加载配置失败！", e);
        }
    }

    public abstract VersionConfig getVersionConfig(VersionUpdate versionUpdate);

    public abstract TerminalPersistedInfo getTerminalInfo(int terminalId);

    public abstract List<AlarmStatus> getTerminalAlarmStatus(int terminalId);

    public Terminal getTerminal(String uid) {
        TerminalContainer container = terminalContainerArray[index];
        return container == null ? null : container.getTerminal(uid);
    }

    public Terminal getTerminal(int id) {
        TerminalContainer container = terminalContainerArray[index];
        return container == null ? null : container.getTerminal(id);
    }

    public TerminalContainer getTerminalContainer() {
        return terminalContainerArray[index];
    }

    public VariableContainer getVariableContainer() {
        return variableContainerArray[index];
    }

    public AlarmContainer getAlarmContainer() {
        return alarmContainerArray[index];
    }

    public void dispatchTerminalDataChangeEvent(Terminal terminal) {
        if (terminalListeners != null) {
            for (TerminalListener listener : terminalListeners) {
                try {
                    listener.dataChangedHandle(terminal);
                } catch (Exception e) {
                    log.error("终端监听数据变更事件异常", e);
                }
            }
        }
    }

    public void dispatchTerminalAlarmTriggerEvent(Terminal terminal, List<AlarmStatus> alarmStatuses) {
        if (terminalListeners != null) {
            for (TerminalListener listener : terminalListeners) {
                try {
                    listener.alarmTriggerHandle(terminal, alarmStatuses);
                } catch (Exception e) {
                    log.error("终端监听报警触发事件异常", e);
                }
            }
        }
    }

    public void dispatchTerminalClosedTriggerEvent(Terminal terminal, List<AlarmStatus> alarmStatuses) {
        if (terminalListeners != null) {
            for (TerminalListener listener : terminalListeners) {
                try {
                    listener.alarmClosedHandle(terminal, alarmStatuses);
                } catch (Exception e) {
                    log.error("终端监听报警关闭事件异常", e);
                }
            }
        }
    }

    public void dispatchTerminalOnlineEvent(Terminal terminal) {
        if (terminalListeners != null) {
            for (TerminalListener listener : terminalListeners) {
                try {
                    listener.terminalOnline(terminal);
                } catch (Exception e) {
                    log.error("终端监听上线事件异常", e);
                }
            }
        }
    }

    public void dispatchTerminalOfflineEvent(Terminal terminal) {
        if (terminalListeners != null) {
            for (TerminalListener listener : terminalListeners) {
                try {
                    listener.terminalOffline(terminal);
                } catch (Exception e) {
                    log.error("终端监听离线事件异常", e);
                }
            }
        }
    }
}
