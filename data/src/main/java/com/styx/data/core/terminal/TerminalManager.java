package com.styx.data.core.terminal;

import com.styx.common.config.GlobalConstants;
import com.styx.common.exception.SystemException;
import com.styx.data.service.InternalMonitorService;
import com.styx.data.service.TerminalDataService;
import com.styx.data.service.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author TontoZhou
 * @since 2020/10/26
 */
@Slf4j
@Component
public class TerminalManager implements ApplicationRunner, Runnable {

    @Autowired
    private Environment environment;

    @Value("${data.config.sync-interval:5}")
    private int configSyncInterval;

    @Value("${data.config.max-maintain-hours:48}")
    private int maxMaintainHours;

    private int dataPersistInterval;

    private String nodeName;

    private volatile String parentNodeCode;
    private volatile int index = 0;
    private Map<String, Terminal>[] terminalMapArray = new Map[2];
    private Map<Integer, Variable>[] variableMapArray = new Map[2];
    private Map<Integer, Alarm>[] alarmMapArray = new Map[2];

    private long variableVersion = -1;
    private long alarmVersion = -1;
    private long terminalVersion = -1;
    private long othersVersion = -1;

    @Autowired
    private InternalMonitorService monitorService;
    @Autowired
    private TerminalDataService terminalDataService;

    @Autowired(required = false)
    private List<TerminalListener> terminalDataListeners;

    private boolean loaded;

    public void loadConfig() {
        try {
            VersionUpdate versionUpdate = new VersionUpdate(nodeName);
            versionUpdate.setAlarmVersion(alarmVersion);
            versionUpdate.setVariableVersion(variableVersion);
            versionUpdate.setOthersVersion(othersVersion);
            versionUpdate.setTerminalVersion(terminalVersion);

            VersionConfig versionConfig = monitorService.getVersionConfig(versionUpdate);

            int nextIndex = (index + 1) % 2;

            long vVersion = versionConfig.getVariableVersion();
            Map<Integer, Variable> variableMap;

            if (vVersion > variableVersion) {
                // 更新变量配置
                List<CVariable> cVariables = versionConfig.getVariables();
                variableMap = new HashMap<>();

                if (cVariables != null) {
                    for (CVariable cVariable : cVariables) {
                        Variable variable = new Variable();
                        variable.setId(cVariable.getId());
                        variable.setName(cVariable.getName());
                        variable.setAddressStart(cVariable.getAddressStart());
                        variable.setSensorType(cVariable.getSensorType());
                        variable.setSwitchAddress(cVariable.getSwitchAddress());
                        variable.setValueType(cVariable.getValueType());
                        variable.setPersisted(cVariable.isPersisted());
                        variable.setMax(cVariable.getMax());
                        variable.setMin(cVariable.getMin());

                        variableMap.put(variable.getId(), variable);
                    }
                }

                variableMapArray[nextIndex] = Collections.unmodifiableMap(variableMap);
                variableVersion = vVersion;

                log.debug("更新变量配置，版本号：" + variableVersion);
            } else {
                variableMapArray[nextIndex] = variableMapArray[index];
            }

            long aVersion = versionConfig.getAlarmVersion();
            List<Alarm> alarmList;

            if (aVersion > alarmVersion) {
                List<CAlarm> cAlarms = versionConfig.getAlarms();
                Map<Integer, Alarm> alarmMap = new HashMap<>();
                alarmList = new ArrayList<>();

                if (cAlarms != null) {
                    for (CAlarm cAlarm : cAlarms) {
                        int aid = cAlarm.getId();
                        Alarm alarm = new Alarm(aid, cAlarm.getName(), cAlarm.getFormula(), cAlarm.getVariableIds());
                        alarmMap.put(aid, alarm);
                        alarmList.add(alarm);
                    }
                }

                alarmMapArray[nextIndex] = Collections.unmodifiableMap(alarmMap);
                alarmVersion = aVersion;

                log.debug("更新报警配置，版本号：" + alarmVersion);
            } else {
                alarmMapArray[nextIndex] = alarmMapArray[index];
            }


            long oVersion = versionConfig.getOthersVersion();
            if (oVersion > othersVersion) {
                COthers others = versionConfig.getOthers();

                // 加载配置
                dataPersistInterval = others.getDataPersistInterval();
            }


            long tVersion = versionConfig.getTerminalVersion();

            if (tVersion > terminalVersion) {
                // 更新终端配置
                List<CTerminal> cTerminals = versionConfig.getTerminals();
                Map<String, Terminal> terminalMap = new HashMap<>();

                if (cTerminals != null) {
                    for (CTerminal cTerminal : cTerminals) {
                        String uid = cTerminal.getUid();
                        try {
                            Terminal terminal = new Terminal(cTerminal, getTerminal(uid),
                                    terminalDataService, terminalDataService, this);
                            terminalMap.put(uid, terminal);
                        } catch (Exception e) {
                            log.error("终端[UID:" + uid + "]加载配置异常", e);
                        }
                    }
                }

                terminalMapArray[nextIndex] = terminalMap;
                terminalVersion = tVersion;

                log.debug("更新终端配置，版本号：" + terminalVersion);
            } else {
                terminalMapArray[nextIndex] = terminalMapArray[index];
            }

            // 切换到配置组,并删除旧配置
            int old = this.index;
            this.index = nextIndex;
            terminalMapArray[old] = null;
            variableMapArray[old] = null;
            alarmMapArray[old] = null;

            parentNodeCode = versionConfig.getParentNodeCode();

            loaded = true;
        } catch (Exception e) {
            log.error("尝试加载配置失败:" + e.getMessage());
        }
    }

    public Terminal getTerminal(String uid) {
        return loaded ? terminalMapArray[index].get(uid) : null;
    }

    public Terminal getTerminal(int id) {
        if (loaded) {
            for (Terminal t : terminalMapArray[index].values()) {
                if (t.getId() == id) {
                    return t;
                }
            }
        }
        return null;
    }

    public Collection<Terminal> getTerminals() {
        return loaded ? terminalMapArray[index].values() : null;
    }

    public Map<Integer, Variable> getVariableMap() {
        return variableMapArray[index];
    }

    public Map<Integer, Alarm> getAlarmMap() {
        return alarmMapArray[index];
    }

    public String getParentNodeCode() {
        return parentNodeCode;
    }


    /**
     * 调度分发终端数据变更事件
     */
    public void dispatchTerminalDataChangeEvent(Terminal terminal) {
        if (terminalDataListeners != null) {
            for (TerminalListener listener : terminalDataListeners) {
                listener.dataChangedHandle(terminal);
            }
        }
    }

    /**
     * 终端开始维护状态
     */
    public void startMaintain(int terminalId, int duration) {
        Terminal terminal = getTerminal(terminalId);
        if (terminal != null) {
            // 不能超过最大维护时长
            if (maxMaintainHours > 0 && maxMaintainHours < duration) {
                duration = maxMaintainHours;
            }
            terminal.toMaintain(duration);
        }
    }

    /**
     * 终端结束维护
     */
    public void offMaintain(int terminalId) {
        Terminal terminal = getTerminal(terminalId);
        if (terminal != null) {
            terminal.offMaintain();
        }
    }

    /**
     * 测试终端转正式
     */
    public void turnFormal(List<Integer> terminalIds) {
        if (terminalIds != null && terminalIds.size() > 0) {
            for (int terminalId : terminalIds) {
                Terminal terminal = getTerminal(terminalId);
                if (terminal != null && terminal.isTest()) {
                    terminal.setTest(false);
                }
            }

            terminalDataService.deleteTestData(terminalIds);
        }
    }

    //-------------------------
    // 持久化数据和终端相关维护
    //-------------------------

    private int runTimes = 0;

    @Override
    public void run() {
        if (!loaded) return;

        Map<String, Terminal> terminalMap = terminalMapArray[index];

        if (terminalMap != null) {
            for (Terminal terminal : terminalMap.values()) {
                terminal.checkOnline();
            }
        }

        if (++runTimes >= dataPersistInterval) {
            if (terminalMap != null) {
                log.debug("持久化一次终端数据");
                terminalDataService.persistData(terminalMap.values());
            }
            runTimes = 0;
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 读取节点名称
        String appName = environment.getProperty("spring.application.name");
        if (!appName.startsWith(GlobalConstants.DATA_SERVICE_PREFIX)) {
            throw new SystemException(SystemException.CODE_ERROR_CONFIG, String.format("#spring.application.name must start with %s", GlobalConstants.DATA_SERVICE_PREFIX));
        }
        this.nodeName = appName.substring(GlobalConstants.DATA_SERVICE_PREFIX.length());

        loadConfig();

        ScheduledExecutorService service1 = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("checkConfig");
                return thread;
            }
        });

        service1.scheduleWithFixedDelay(() -> loadConfig(), 10, configSyncInterval, TimeUnit.SECONDS);

        ScheduledExecutorService service2 = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("checkData");
                return thread;
            }
        });

        service2.scheduleWithFixedDelay(this, 1, 1, TimeUnit.MINUTES);

    }


}
