package com.styx.monitor.service.data;

import com.styx.common.cache.DataCacheManager;
import com.styx.common.exception.BusinessException;
import com.styx.monitor.mapper.config.ConfigTerminalMapper;
import com.styx.monitor.service.config.TerminalService;
import com.styx.monitor.service.config.dto.StationTerminal;
import com.styx.monitor.service.data.vo.TerminalRealData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author TontoZhou
 * @since 2020/11/19
 */
@Slf4j
@Service
public class TerminalDataService {

    @Autowired
    private DataMicroService dataMicroService;

    @Autowired
    private ConfigTerminalMapper terminalMapper;

    @Value("${monitor.terminal.data.history.max-time-span:31}")
    private int terminalHistoryDataMaxTimeSpan;


    public TerminalRealData getTerminalDataRealtime(int terminalId) {
        StationTerminal terminal = terminalMapper.getEnabledStationTerminal(terminalId);
        if (terminal == null) {
            throw new BusinessException("查找的终端不存在或未启用");
        }
        return dataMicroService.getTerminalDataRealtime(terminal.getNodeCode(), terminalId);
    }


}
