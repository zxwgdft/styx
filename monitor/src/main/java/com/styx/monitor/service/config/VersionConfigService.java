package com.styx.monitor.service.config;

import com.styx.monitor.mapper.config.ConfigAlarmMapper;
import com.styx.monitor.mapper.config.ConfigTerminalMapper;
import com.styx.monitor.mapper.config.ConfigVariableMapper;
import com.styx.monitor.mapper.config.ConfigVersionMapper;
import com.styx.monitor.model.config.ConfigVersion;
import com.styx.monitor.service.config.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/3/29
 */
@Slf4j
@Service
public class VersionConfigService {

    public static final String CONFIG_ALARM = "config_alarm";
    public static final String CONFIG_TERMINAL = "config_terminal";
    public static final String CONFIG_VARIABLE = "config_variable";

    @Autowired
    private ConfigVersionMapper configVersionMapper;

    @Autowired
    private ConfigAlarmMapper configAlarmMapper;

    @Autowired
    private ConfigTerminalMapper configTerminalMapper;

    @Autowired
    private ConfigVariableMapper configVariableMapper;


    public void incrementVersion(String configId) {
        configVersionMapper.addVersion(configId);
    }

    public VersionConfig getVersionConfig(VersionUpdate versionUpdate) {
        List<ConfigVersion> configVersions = configVersionMapper.getVersionList();

        String serverNode = versionUpdate.getServerNode();

        VersionConfig versionConfig = new VersionConfig();
        for (ConfigVersion configVersion : configVersions) {
            String id = configVersion.getId();
            long version = configVersion.getVersion();

            if (CONFIG_ALARM.equals(id)) {
                if (versionUpdate.getAlarmVersion() < version) {
                    versionConfig.setAlarms(readAlarms(serverNode));
                    versionConfig.setAlarmVersion(version);
                } else {
                    versionConfig.setAlarmVersion(versionUpdate.getAlarmVersion());
                }
            } else if (CONFIG_TERMINAL.equals(id)) {
                if (versionUpdate.getTerminalVersion() < version) {
                    versionConfig.setTerminals(readTerminals(serverNode));
                    versionConfig.setTerminalVersion(version);
                } else {
                    versionConfig.setTerminalVersion(versionUpdate.getTerminalVersion());
                }
            } else if (CONFIG_VARIABLE.equals(id)) {
                if (versionUpdate.getVariableVersion() < version) {
                    versionConfig.setVariables(readVariables(serverNode));
                    versionConfig.setVariableVersion(version);
                } else {
                    versionConfig.setVariableVersion(versionUpdate.getAlarmVersion());
                }
            }
        }

        return versionConfig;
    }

    private List<CVariable> readVariables(String serverNode) {
        return configVariableMapper.findVariableConfigList();
    }

    private List<CTerminal> readTerminals(String serverNode) {
        return configTerminalMapper.findTerminalConfigList(serverNode);
    }

    private List<CAlarm> readAlarms(String serverNode) {
        return configAlarmMapper.findAlarmConfigList();
    }


}
