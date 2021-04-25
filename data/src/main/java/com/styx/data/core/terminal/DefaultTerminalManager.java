package com.styx.data.core.terminal;

import com.styx.common.config.GlobalConstants;
import com.styx.common.exception.SystemException;
import com.styx.data.mapper.TerminalAlarmMapper;
import com.styx.data.mapper.TerminalInfoMapper;
import com.styx.data.model.TerminalInfo;
import com.styx.data.service.InternalMonitorService;
import com.styx.data.service.TerminalDataService;
import com.styx.data.service.dto.VersionConfig;
import com.styx.data.service.dto.VersionUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
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
public class DefaultTerminalManager extends AbstractTerminalManager implements ApplicationRunner {

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
    private TerminalInfoMapper terminalInfoMapper;

    @Autowired
    private TerminalDataService terminalDataService;


    //-------------------------
    // 持久化数据和终端相关维护
    //-------------------------

    private int runTimes = 0;

    public void checkTerminal() {
        TerminalContainer terminalContainer = getTerminalContainer();

        if (terminalContainer != null) {
            for (Terminal terminal : terminalContainer.getTerminals()) {
                terminal.checkOnline();
            }
        }

        if (++runTimes >= dataPersistInterval) {
            if (terminalContainer != null) {
                log.debug("持久化一次终端数据");
                terminalDataService.persistData(terminalContainer.getTerminals());
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

        ScheduledExecutorService service1 = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("checkConfig");
                return thread;
            }
        });

        service1.scheduleWithFixedDelay(() -> loadConfig(), 0, configSyncInterval, TimeUnit.SECONDS);

        ScheduledExecutorService service2 = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("checkTerminal");
                return thread;
            }
        });

        service2.scheduleWithFixedDelay(() -> checkTerminal(), 1, 1, TimeUnit.MINUTES);
    }


    @Override
    public VersionConfig getVersionConfig(VersionUpdate versionUpdate) {
        return monitorService.getVersionConfig(versionUpdate);
    }

    @Override
    public TerminalInfo getTerminalInfo(int terminalId) {
        return terminalInfoMapper.selectById(terminalId);
    }

    @Override
    public List<AlarmStatus> getTerminalAlarmStatus(int terminalId) {
        return terminalAlarmMapper.getAlarmIdOfTerminal(terminalId);
    }
}
