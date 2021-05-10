package com.styx.monitor.service.config;

import com.styx.common.exception.BusinessException;
import com.styx.monitor.core.MonitorServiceSupport;
import com.styx.monitor.mapper.config.ConfigTerminalMapper;
import com.styx.monitor.model.config.ConfigTerminal;
import com.styx.monitor.service.config.vo.StationTerminalVO;
import com.styx.monitor.service.config.vo.TerminalDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TerminalService extends MonitorServiceSupport<ConfigTerminal> {

    @Autowired
    private VariableService variableService;

    @Autowired
    private ConfigTerminalMapper terminalMapper;

    public List<StationTerminalVO> getEnabledStationTerminalByNode(String node) {
        return terminalMapper.findEnabledStationTerminalListByNode(node);
    }

    public TerminalDetailVO getTerminalDetail(int terminalId) {
        TerminalDetailVO detail = terminalMapper.getStationTerminalDetail(terminalId);
        if (detail == null) {
            throw new BusinessException("查找的终端不存在或未启用");
        }
        detail.setVariables(variableService.getEnabledVariables(detail.getVariableIds()));
        return detail;
    }

}
