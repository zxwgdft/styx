package com.styx.monitor.service.data;

import com.styx.common.exception.BusinessException;
import com.styx.monitor.mapper.config.ConfigTerminalMapper;
import com.styx.monitor.service.config.dto.StationTerminal;
import com.styx.monitor.service.data.vo.TerminalFlow;
import com.styx.monitor.service.data.vo.TerminalRealData;
import com.styx.monitor.service.data.vo.TerminalSimpleRealData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Value("${monitor.terminal.data.history.max-time-span:31}")
    private int terminalHistoryDataMaxTimeSpan;

    @Value("${data.config.flow-rank-key:flowRank}")
    private String flowRankKey;

    /**
     * 获取某终端详细的实时数据（包括运行状态、变量数据、报警数据）
     *
     * @param terminalId 终端ID
     */
    public TerminalRealData getTerminalDataRealtime(int terminalId) {
        StationTerminal terminal = terminalMapper.getEnabledStationTerminal(terminalId);
        if (terminal == null) {
            throw new BusinessException("查找的终端不存在或未启用");
        }
        return dataMicroService.getTerminalDataRealtime(terminal.getNodeCode(), terminalId);
    }

    /**
     * 获取某数据节点下所有终端简单实时数据（是否在线、是否报警等数据）
     *
     * @param node 数据节点编码
     */
    public TerminalSimpleRealData[] getTerminalSimpleDataRealtime(String node) {
        return dataMicroService.getTerminalSimpleDataRealtime(node);
    }

    /**
     * 获取流量排行数据
     *
     * @param size 排行榜长度
     */
    public List<TerminalFlow> getFlowRank(int size) {
        return getFlowRank(size);
    }

    private List<TerminalFlow> getFlowRank(int size, int count) {
        if (count == 10) throw new BusinessException("获取累计流量排行榜数据异常");

        if (size <= 0) size = 10;
        else if (size > 100) size = 100;

        Set<ZSetOperations.TypedTuple<String>> rankSet = redisTemplate.opsForZSet().rangeWithScores(flowRankKey, 0, size - 1);
        if (rankSet == null || rankSet.size() == 0) return Collections.emptyList();

        StringBuilder ids = new StringBuilder();
        for (ZSetOperations.TypedTuple<String> item : rankSet) {
            ids.append(item.getValue()).append(',');
        }
        ids.deleteCharAt(ids.length() - 1);

        List<TerminalFlow> terminalFlows = terminalMapper.findTerminalListForFlow(ids.toString());
        if (terminalFlows == null || terminalFlows.size() == 0) return Collections.emptyList();

        Map<Integer, TerminalFlow> terminalFlowMap = new HashMap<>((int) (terminalFlows.size() / .75) + 1);
        for (TerminalFlow item : terminalFlows) terminalFlowMap.put(item.getId(), item);


        if (terminalFlows.size() == rankSet.size()) {
            // 没有过期的终端数据
            for (ZSetOperations.TypedTuple<String> item : rankSet) {
                int id = Integer.valueOf(item.getValue());
                TerminalFlow terminalFlow = terminalFlowMap.get(id);
                terminalFlow.setFlow(item.getScore());
            }
            return terminalFlows;
        } else {


            return getFlowRank(size);
        }
    }

}
