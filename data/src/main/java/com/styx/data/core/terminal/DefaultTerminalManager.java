package com.styx.data.core.terminal;

import com.styx.common.config.GlobalConstants;
import com.styx.common.exception.SystemException;
import com.styx.data.mapper.SysMapMapper;
import com.styx.data.mapper.TerminalAlarmMapper;
import com.styx.data.model.TerminalInfo;
import com.styx.data.service.InternalMonitorService;
import com.styx.data.service.TerminalDataService;
import com.styx.data.service.dto.VersionConfig;
import com.styx.data.service.dto.VersionUpdate;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private SysMapMapper terminalInfoMapper;

    @Autowired
    private TerminalDataService terminalDataService;

    @Autowired
    private EventExecutorGroup eventExecutorGroup;

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

    public void persistTerminal() {
        
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 读取节点名称
        String appName = environment.getProperty("spring.application.name");
        if (!appName.startsWith(GlobalConstants.DATA_SERVICE_PREFIX)) {
            throw new SystemException(SystemException.CODE_ERROR_CONFIG, String.format("#spring.application.name must start with %s", GlobalConstants.DATA_SERVICE_PREFIX));
        }
        this.nodeName = appName.substring(GlobalConstants.DATA_SERVICE_PREFIX.length());

        log.info("开启定时执行加载终端配置任务");
        eventExecutorGroup.scheduleWithFixedDelay(() -> loadConfig(), 0, configSyncInterval, TimeUnit.SECONDS);
        log.info("开启定时执行检查终端任务");
        eventExecutorGroup.scheduleWithFixedDelay(() -> checkTerminal(), 1, 1, TimeUnit.MINUTES);
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


    @Getter
    @Setter
    private static class TerminalSnapshot {
        // 终端ID
        private int id;
        // 工作总时间
        private int wtt;
        // 最近登录时间
        private long llt;
        // 最近工作时间（数据更新时间）
        private long lwt;
    }
}
