package com.paladin.monitor.service.data;

import com.paladin.framework.common.R;
import com.paladin.framework.exception.BusinessException;
import com.paladin.monitor.core.DataPermissionUtil;
import com.paladin.monitor.core.MonitorUserSession;
import com.paladin.monitor.core.config.CTerminal;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.styx.common.api.R;
import com.styx.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author TontoZhou
 * @since 2021/1/15
 */
@Slf4j
@Service
public class TerminalControlService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CTerminalContainer terminalContainer;


    @Value("${monitor.config.max-maintain-hours:48}")
    private int maxMaintainHours;

    public void startMaintain(int terminalId, int duration) {
        CTerminal terminal = terminalContainer.getTerminal(terminalId);
        if (terminal == null || !terminal.isEnabled()) {
            throw new BusinessException("终端不存在或未被启用");
        }

        if (!DataPermissionUtil.hasStationPermission(MonitorUserSession.getCurrentUserSession(), terminal)) {
            throw new BusinessException("没有权限获取数据");
        }

        if (duration <= 0 || duration > maxMaintainHours) {
            throw new BusinessException("终端维护时长必须在0到" + maxMaintainHours + "小时之间");
        }

        String serverNode = terminal.getNodeCode();
        try {
            String url = "http://msms-data-" + serverNode + "/terminal/control/maintain/start?terminalId=" + terminalId + "&duration=" + duration;
            restTemplate.getForEntity(url, R.class);
        } catch (Exception e) {
            log.error("开启终端[" + terminalId + "]维护异常", e);
            throw new BusinessException("开启终端维护异常");
        }
    }

    public void offMaintain(int terminalId) {
        CTerminal terminal = terminalContainer.getTerminal(terminalId);
        if (terminal == null || !terminal.isEnabled()) {
            throw new BusinessException("终端不存在或未被启用");
        }

        if (!DataPermissionUtil.hasStationPermission(MonitorUserSession.getCurrentUserSession(), terminal)) {
            throw new BusinessException("没有权限获取数据");
        }

        String serverNode = terminal.getNodeCode();
        try {
            String url = "http://msms-data-" + serverNode + "/terminal/control/maintain/off?terminalId=" + terminalId;
            restTemplate.getForEntity(url, R.class);
        } catch (Exception e) {
            log.error("关闭终端[" + terminalId + "]维护异常", e);
            throw new BusinessException("关闭终端维护异常");
        }
    }


}
