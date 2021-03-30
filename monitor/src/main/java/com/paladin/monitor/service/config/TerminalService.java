package com.paladin.monitor.service.config;

import com.paladin.monitor.mapper.config.ConfigTerminalMapper;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.service.config.dto.StationTerminal;
import com.styx.common.service.ServiceSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TerminalService extends ServiceSupport<ConfigTerminal> {

    @Autowired
    private ConfigTerminalMapper terminalMapper;

    public StationTerminal getEnabledStationTerminal(int terminalId){
        return terminalMapper.getEnabledStationTerminal(terminalId);
    }

}
