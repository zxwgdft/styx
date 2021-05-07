package com.styx.monitor.service.data;

import com.styx.common.exception.BusinessException;
import com.styx.common.utils.StringUtil;
import com.styx.monitor.service.data.dto.TerminalDataQuery;
import com.styx.monitor.service.data.vo.AlarmStatus;
import com.styx.monitor.service.data.vo.TerminalAlarms;
import com.styx.monitor.service.data.vo.TerminalRealData;
import com.styx.monitor.service.data.vo.TerminalSimpleRealData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author TontoZhou
 * @since 2021/4/30
 */
@Slf4j
@Service
public class DataMicroService {

    private static final String service_prefix = "http://styx-data-";

    @Autowired
    private RestTemplate restTemplate;

    public TerminalSimpleRealData[] getTerminalSimpleDataRealtime(String node) {
        String url = service_prefix + node + "/terminal/data/get/real/simple";
        try {
            return restTemplate.getForEntity(url, TerminalSimpleRealData[].class).getBody();
        } catch (Exception e) {
            log.error("获取数据服务节点简单实时数据异常", e);
            throw new BusinessException("获取简单实时数据失败");
        }
    }

    public TerminalRealData getTerminalDataRealtime(String node, int terminalId) {
        String url = service_prefix + node + "/terminal/data/get/real/detail?terminalId=" + terminalId;
        try {
            return restTemplate.getForEntity(url, TerminalRealData.class).getBody();
        } catch (Exception e) {
            log.error("获取数据服务节点详细实时数据异常", e);
            throw new BusinessException("获取详细实时数据失败");
        }
    }

    public TerminalAlarms[] getAllTriggeringAlarms(String node) {
        String url = service_prefix + node + "/terminal/data/get/alarm/all";
        try {
            return restTemplate.getForEntity(url, TerminalAlarms[].class).getBody();
        } catch (Exception e) {
            log.error("获取数据服务节点所有报警数据异常", e);
            throw new BusinessException("获取所有报警数据失败");
        }
    }


    public AlarmStatus[] getTerminalTriggeringAlarms(String node, int terminalId) {
        String url = service_prefix + node + "/terminal/data/get/alarm?terminalId=" + terminalId;
        try {
            return restTemplate.getForEntity(url, AlarmStatus[].class).getBody();
        } catch (Exception e) {
            log.error("获取数据服务节点终端报警数据异常", e);
            throw new BusinessException("获取终端报警数据失败");
        }
    }

    public String getTerminalHistoryData(String node, TerminalDataQuery query) {
        String url = service_prefix + node + "/terminal/data/find/history";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TerminalDataQuery> entity = new HttpEntity<>(query, headers);
            return restTemplate.postForEntity(url, entity, String.class).getBody();
        } catch (Exception e) {
            log.error("获取终端历史数据异常", e);
            throw new BusinessException("获取终端历史数据失败");
        }
    }
}
