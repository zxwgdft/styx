package com.styx.monitor.service.config;

import com.styx.monitor.core.MonitorServiceSupport;
import com.styx.monitor.mapper.config.ConfigTerminalMapper;
import com.styx.monitor.model.config.ConfigTerminal;
import com.styx.monitor.service.config.dto.StationTerminal;
import com.styx.common.service.ServiceSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TerminalService extends MonitorServiceSupport<ConfigTerminal> {

    @Autowired
    private ConfigTerminalMapper terminalMapper;

    public StationTerminal getEnabledStationTerminal(int terminalId){
        return terminalMapper.getEnabledStationTerminal(terminalId);
    }

}
