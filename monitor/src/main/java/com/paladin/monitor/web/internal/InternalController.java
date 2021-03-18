package com.paladin.monitor.web.internal;

import com.paladin.monitor.core.config.CAlarmContainer;
import com.paladin.monitor.core.config.COthersContainer;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.config.CVariableContainer;
import com.paladin.monitor.web.internal.dto.VersionConfig;
import com.paladin.monitor.web.internal.dto.VersionUpdate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TontoZhou
 * @since 2020/10/30
 */
@Api(tags = "内部参数配置接口")
@RestController
@RequestMapping("/internal/")
public class InternalController {

    @Autowired
    private CVariableContainer variableContainer;

    @Autowired
    private CTerminalContainer terminalContainer;

    @Autowired
    private CAlarmContainer alarmContainer;

    @Autowired
    private COthersContainer othersContainer;


    @ApiOperation("配置版本更新")
    @PostMapping("/config/update")
    public VersionConfig getVariable(@RequestBody VersionUpdate versionUpdate) {
        VersionConfig versionConfig = new VersionConfig();

        if (variableContainer.getVersion() > versionUpdate.getVariableVersion()) {
            versionConfig.setVariableVersion(variableContainer.getVersion());
            versionConfig.setVariables(variableContainer.getCVariables());
        } else {
            versionConfig.setVariableVersion(versionUpdate.getVariableVersion());
        }

        if (alarmContainer.getVersion() > versionUpdate.getAlarmVersion()) {
            versionConfig.setAlarmVersion(alarmContainer.getVersion());
            versionConfig.setAlarms(alarmContainer.getEnabledAlarms());
        } else {
            versionConfig.setAlarmVersion(versionUpdate.getAlarmVersion());
        }

        if (othersContainer.getVersion() > versionUpdate.getOthersVersion()) {
            versionConfig.setOthersVersion(othersContainer.getVersion());
            versionConfig.setOthers(othersContainer.getOthers());
        } else {
            versionConfig.setOthersVersion(versionUpdate.getOthersVersion());
        }

        if (terminalContainer.getVersion() > versionUpdate.getTerminalVersion()) {
            versionConfig.setTerminalVersion(terminalContainer.getVersion());
            versionConfig.setTerminals(terminalContainer.getTerminalOfServerNode(versionUpdate.getServerNode()));
        } else {
            versionConfig.setTerminalVersion(versionUpdate.getTerminalVersion());
        }

        versionConfig.setParentNodeCode(terminalContainer.getParentServerNodeCode(versionUpdate.getServerNode()));

        return versionConfig;
    }


}
