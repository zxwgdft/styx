package com.styx.monitor.service.data;

import com.styx.common.config.GlobalConstants;
import com.styx.common.config.RedisConstants;
import com.styx.common.config.ZKConstants;
import com.styx.common.exception.BusinessException;
import com.styx.monitor.mapper.config.ConfigNodeMapper;
import com.styx.monitor.mapper.config.ConfigTerminalMapper;
import com.styx.monitor.mapper.data.TerminalDataMapper;
import com.styx.monitor.service.config.dto.StationTerminal;
import com.styx.monitor.service.data.vo.NodeReaData;
import com.styx.monitor.service.data.vo.TerminalFlow;
import com.styx.monitor.service.data.vo.TerminalRealData;
import com.styx.monitor.service.data.vo.TerminalSimpleRealData;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ConfigNodeMapper nodeMapper;

    @Autowired
    private TerminalDataMapper terminalDataMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CuratorFramework curatorFramework;

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
        return getFlowRank(size, 1);
    }

    private List<TerminalFlow> getFlowRank(int size, int count) {
        if (count == 10) throw new BusinessException("获取累计流量排行榜数据异常");

        if (size <= 0) size = 10;
        else if (size > 100) size = 100;

        Set<ZSetOperations.TypedTuple<String>> rankSet = redisTemplate.opsForZSet().reverseRangeWithScores(RedisConstants.KEY_ZSET_FLOW, 0, size - 1);
        if (rankSet == null || rankSet.size() == 0) return Collections.emptyList();

        StringBuilder ids = new StringBuilder();
        for (ZSetOperations.TypedTuple<String> item : rankSet) {
            ids.append(item.getValue()).append(',');
        }
        ids.deleteCharAt(ids.length() - 1);

        int rankSize = rankSet.size();
        List<TerminalFlow> terminalFlows = terminalDataMapper.findTerminalListForFlow(ids.toString());
        if (terminalFlows != null && terminalFlows.size() > 0) {
            Map<Integer, TerminalFlow> terminalFlowMap = new HashMap<>((int) (terminalFlows.size() / .75) + 1);
            for (TerminalFlow item : terminalFlows) terminalFlowMap.put(item.getId(), item);
            if (terminalFlows.size() == rankSize) {
                // 没有过期的终端数据
                List<TerminalFlow> sorted = new ArrayList<>(rankSize);
                for (ZSetOperations.TypedTuple<String> item : rankSet) {
                    int id = Integer.valueOf(item.getValue());
                    TerminalFlow terminalFlow = terminalFlowMap.get(id);
                    terminalFlow.setFlow(item.getScore());
                    sorted.add(terminalFlow);
                }
                return sorted;
            } else {
                // 删除不存在的终端后重新查询
                String[] arr = new String[rankSize];
                int i = 0;
                for (ZSetOperations.TypedTuple<String> item : rankSet) {
                    String id = item.getValue();
                    if (terminalFlowMap.get(Integer.valueOf(id)) == null) {
                        arr[i++] = id;
                    }
                }

                String[] remArr = new String[i];
                System.arraycopy(arr, 0, remArr, 0, i);
                redisTemplate.opsForZSet().remove(RedisConstants.KEY_ZSET_FLOW, remArr);
                return getFlowRank(size, count + 1);
            }
        } else {
            // 删除不存在的终端后重新查询
            String[] remArr = new String[rankSize];
            int i = 0;
            for (ZSetOperations.TypedTuple<String> item : rankSet) {
                remArr[i++] = item.getValue();
            }
            redisTemplate.opsForZSet().remove(RedisConstants.KEY_ZSET_FLOW, remArr);
            return getFlowRank(size, count + 1);
        }
    }


    /**
     * 获取节点实时数据
     */
    public List<NodeReaData> getNodeRealtime() {
        List<NodeReaData> list = nodeMapper.findRealList();
        try {
            List<String> zkNodes = curatorFramework.getChildren().forPath(ZKConstants.PATH_DATA_SERVER);
            if (zkNodes != null && zkNodes.size() > 0) {
                Set<String> zkNodeSet = new HashSet<>();
                zkNodes.stream().forEach((item) -> {
                            zkNodeSet.add(item.substring(GlobalConstants.DATA_SERVICE_PREFIX.length()));
                        }
                );

                list.stream().forEach((item) -> {
                    if (zkNodeSet.contains(item.getCode())) item.setOnline(true);
                });
            }
        } catch (Exception e) {
            throw new BusinessException("获取数据节点状态异常", e);
        }
        return list;
    }
}
