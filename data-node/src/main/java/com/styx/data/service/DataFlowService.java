package com.styx.data.service;

import com.styx.common.config.RedisConstants;
import com.styx.common.utils.StringUtil;
import com.styx.common.utils.convert.JsonUtil;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalListener;
import com.styx.data.mapper.TerminalDataMapper;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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

    private final static String KEY_FLOW = "snapshot_flow";

    @Autowired
    private PersistedMapService persistedMapService;

    @Autowired
    private TerminalDataMapper terminalDataMapper;

    @Autowired
    private EventExecutorGroup eventExecutorGroup;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${styx.data.protocol.vid-ljll}")
    private int variable_id_ljll;

    private Map<Integer, Float> totalFlowMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() throws IOException {
        String json = persistedMapService.getText(KEY_FLOW);
        if (!StringUtil.isEmpty(json)) {
            Map<?, ?> map = JsonUtil.parseJson(json, Map.class);
            for (Map.Entry entry : map.entrySet()) {
                String key = (String) entry.getKey();
                Number value = (Number) entry.getValue();
                totalFlowMap.put(Integer.valueOf(key), value.floatValue());
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
        try {
            persistedMapService.putObject(KEY_FLOW, totalFlowMap);

            if (totalFlowMap.size() > 0) {
                // 放入redis进行排行计算
                Set<ZSetOperations.TypedTuple<String>> values = new HashSet<>((int) (totalFlowMap.size() / .75) + 1);
                for (Iterator<Map.Entry<Integer, Float>> iterator = totalFlowMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<Integer, Float> entry = iterator.next();
                    values.add(new DefaultTypedTuple<>(entry.getKey().toString(), entry.getValue().doubleValue()));
                }
                redisTemplate.opsForZSet().add(RedisConstants.KEY_ZSET_FLOW, values);
            }

            log.debug("持久化累计流量统计");
        } catch (IOException e) {
            log.error("持久化累计流量异常", e);
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
