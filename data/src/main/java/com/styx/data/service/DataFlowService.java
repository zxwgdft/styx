package com.styx.data.service;

import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalListener;
import com.styx.data.mapper.TerminalDataFlowMapper;
import com.styx.data.mapper.TerminalDataMapper;
import com.styx.data.model.TerminalDataFlow;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 累计流量数据服务
 *
 * @author TontoZhou
 * @since 2020/11/18
 */
@Slf4j
@Component
public class DataFlowService implements TerminalListener, ApplicationRunner {

    @Autowired
    private TerminalDataFlowMapper terminalDataFlowMapper;

    @Autowired
    private TerminalDataMapper terminalDataMapper;

    @Autowired
    private EventExecutorGroup eventExecutorGroup;


    @Value("${data.protocol.variable.id-ljll}")
    private int variable_id_ljll;

    private Map<Integer, Float> totalFlowMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<TerminalDataFlow> data = terminalDataFlowMapper.getDataFlow();
        if (data != null) {
            for (TerminalDataFlow item : data) {
                totalFlowMap.putIfAbsent(item.getTerminalId(), item.getFlowValue());
            }
        }

    }


    @Override
    public void dataChangedHandle(Terminal terminal) {
        Map<Integer, Float> variableValueMap = terminal.getVariableValueMap();
        if (variableValueMap != null) {
            Float value = variableValueMap.get(variable_id_ljll);
            if (value != null) {
                totalFlowMap.put(terminal.getId(), value);
            }
        }
    }

    /**
     * 获取所有终端累计流量
     */
    public Map<Integer, Float> getTotalFlowMap() {
        return totalFlowMap;
    }


    /**
     * 持久化流量数据
     */
    public void persistTotalFlowData() {
        for (Map.Entry<Integer, Float> entry : totalFlowMap.entrySet()) {
            if (terminalDataFlowMapper.updateFlowValue(entry.getKey(), entry.getValue()) <= 0) {
                terminalDataFlowMapper.insertFlowValue(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 获取终端累计流量
     */
    public Float getTotalFlowOfTerminal(int terminalId) {
        return totalFlowMap.get(terminalId);
    }


    /**
     * 获取终端时间段内累计流量
     */
    public Float getTotalFlowOfTerminalOfTime(int terminalId, int startDay, int endDay) {
        return terminalDataMapper.getBalanceOfTerminalByTime(terminalId, startDay, endDay, variable_id_ljll);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开启定时执行持久化累计流量任务");
        eventExecutorGroup.scheduleWithFixedDelay(() -> persistTotalFlowData(), 2, 2, TimeUnit.MINUTES);
    }


}
