package com.styx.monitor.service.data;

import com.styx.common.cache.DataCacheManager;
import com.styx.monitor.service.config.TerminalService;
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
    private DataCacheManager dataCacheManager;

    @Autowired
    private TerminalService terminalService;


    @Value("${monitor.terminal.data.history.default-time-span:10}")
    private int terminalHistoryDataDefaultTimeSpan;

    @Value("${monitor.terminal.data.history.max-time-span:31}")
    private int terminalHistoryDataMaxTimeSpan;


}
