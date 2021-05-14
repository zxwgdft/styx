package com.styx.data.core.terminal;

import com.styx.common.config.GlobalConstants;
import com.styx.common.exception.SystemException;
import com.styx.common.spring.SpringBeanHelper;
import com.styx.data.mapper.TerminalAlarmMapper;
import com.styx.data.service.InternalMonitorService;
import com.styx.data.service.PersistedMapService;
import com.styx.data.service.TerminalDataService;
import com.styx.data.service.dto.VersionConfig;
import com.styx.data.service.dto.VersionUpdate;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author TontoZhou
 * @since 2020/10/26
 */
@Slf4j
@Component
public class DefaultTerminalManager extends AbstractTerminalManager implements ApplicationRunner {

    private final static String KEY_TERMINAL = "snapshot_terminal";

    @Autowired
    private Environment environment;

    @Value("${data.config.sync-interval:5}")
    private int configSyncInterval;

    @Value("${data.config.data-persist-interval:10}")
    private int dataPersistInterval;


    @Autowired
    private InternalMonitorService monitorService;

    @Autowired
    private TerminalAlarmMapper terminalAlarmMapper;

    @Autowired
    private PersistedMapService persistedMapService;

    @Autowired
    private TerminalDataService terminalDataService;

    @Autowired
    private EventExecutorGroup eventExecutorGroup;


    private Map<Integer, TerminalPersistedInfo> terminalPersistedInfoMap;

    @PostConstruct
    public void init() throws IOException {
        List<TerminalPersistedInfo> list = persistedMapService.getObjectList(KEY_TERMINAL, TerminalPersistedInfo.class);
        if (list != null) {
            Map<Integer, TerminalPersistedInfo> terminalPersistedInfoMap = new ConcurrentHashMap<>(Math.max((int) (list.size() / .75) + 1, 16));
            for (TerminalPersistedInfo info : list) {
                terminalPersistedInfoMap.put(info.getId(), info);
            }

            this.terminalPersistedInfoMap = terminalPersistedInfoMap;
        } else {
            this.terminalPersistedInfoMap = new ConcurrentHashMap<>();
        }
    }


    //-------------------------
    // 持久化数据和终端相关维护
    //-------------------------


    public void persistData() {
        TerminalContainer terminalContainer = getTerminalContainer();
        if (terminalContainer != null) {
            terminalDataService.persistData(terminalContainer.getTerminals());
            log.debug("完成一次终端数据持久化");
        }
    }

    public void checkTerminal() {
        TerminalContainer terminalContainer = getTerminalContainer();
        if (terminalContainer != null) {
            for (Terminal terminal : terminalContainer.getTerminals()) {
                terminal.checkOnline();
            }
            log.debug("完成一次所有终端状态");
        }
    }

    public void persistTerminalStatus() {
        TerminalContainer terminalContainer = getTerminalContainer();
        if (terminalContainer != null) {
            List<Terminal> terminalList = terminalContainer.getTerminals();

            for (Terminal terminal : terminalList) {
                TerminalPersistedInfo item = new TerminalPersistedInfo();
                item.setId(terminal.getId());
                item.setWorkTotalTime(terminal.getWorkTotalTime());
                item.setLastLoginTime(terminal.getLastLoginTime());
                item.setDataUpdateTime(terminal.getDataUpdateTime());

                terminalPersistedInfoMap.put(item.getId(), item);
            }

            if (terminalPersistedInfoMap.size() > 0) {
                try {
                    persistedMapService.putObject(KEY_TERMINAL, terminalPersistedInfoMap.values());
                    log.debug("完成一次所有终端状态持久化");
                } catch (IOException e) {
                    log.error("持久化终端状态异常", e);
                }
            }
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

        log.info("当前数据采集节点为：{}", nodeName);

        Map<String, TerminalListener> listenerMap = SpringBeanHelper.getBeansByType(TerminalListener.class);
        if (listenerMap != null && listenerMap.size() > 0) {
            List<TerminalListener> listeners = new ArrayList<>(listenerMap.size());
            listeners.addAll(listenerMap.values());
            this.terminalListeners = listeners;
            log.info("加载{}个终端监听器", listeners.size());
        }


        log.info("开启定时执行加载终端配置任务");
        eventExecutorGroup.scheduleWithFixedDelay(() -> loadConfig(), 0, configSyncInterval, TimeUnit.SECONDS);
        log.info("开启定时执行检查终端任务");
        eventExecutorGroup.scheduleWithFixedDelay(() -> checkTerminal(), 1, 1, TimeUnit.MINUTES);
        log.info("开启定时执行持久化终端状态任务");
        eventExecutorGroup.scheduleWithFixedDelay(() -> persistTerminalStatus(), 1, 2, TimeUnit.MINUTES);
        log.info("开启定时执行持久化终端数据任务");
        eventExecutorGroup.scheduleWithFixedDelay(() -> persistData(), 1, dataPersistInterval, TimeUnit.MINUTES);


    }

    @Override
    public VersionConfig getVersionConfig(VersionUpdate versionUpdate) {
        return monitorService.getVersionConfig(versionUpdate);
    }

    @Override
    public TerminalPersistedInfo getTerminalInfo(int terminalId) {
        return terminalPersistedInfoMap.get(terminalId);
    }

    @Override
    public List<AlarmStatus> getTerminalAlarmStatus(int terminalId) {
        return terminalAlarmMapper.getAlarmIdOfTerminal(terminalId);
    }

}
