package com.paladin.monitor.service.data;

import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.utils.TimeUtil;
import com.paladin.monitor.core.DataPermissionUtil;
import com.paladin.monitor.core.MonitorUserSession;
import com.paladin.monitor.core.config.CAlarmContainer;
import com.paladin.monitor.core.config.CTerminal;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.config.CVariableContainer;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.service.data.dto.*;
import com.paladin.monitor.service.config.TerminalService;
import com.paladin.monitor.service.config.dto.StationDeviceQuery;
import com.paladin.monitor.service.config.vo.StationDeviceMonitorVO;
import com.styx.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/11/19
 */
@Slf4j
@Service
public class TerminalDataService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CTerminalContainer terminalContainer;

    @Autowired
    private CAlarmContainer alarmContainer;

    @Autowired
    private CVariableContainer variableContainer;

    @Autowired
    private TerminalService stationDeviceService;


    @Value("${monitor.terminal.data.history.default-time-span:10}")
    private int terminalHistoryDataDefaultTimeSpan;

    @Value("${monitor.terminal.data.history.max-time-span:31}")
    private int terminalHistoryDataMaxTimeSpan;

    @Value("${monitor.terminal.data.history.variable:30,31}")
    private String terminalHistoryDataVariable;

    /**
     * 获取终端实时数据
     */
    public TerminalRealtime getRealTimeData(int terminalId) {
        CTerminal terminal = terminalContainer.getTerminal(terminalId);
        if (terminal == null || !terminal.isEnabled()) {
            throw new BusinessException("终端不存在或未被启用");
        }

        if (!DataPermissionUtil.hasStationPermission(MonitorUserSession.getCurrentUserSession(), terminal)) {
            throw new BusinessException("您没有权限获取该终端实时数据");
        }

        String serverNode = terminal.getNodeCode();
        try {
            String url = "http://msms-data-" + serverNode + "/terminal/data/realtime?terminalIds=" + terminalId;
            TerminalRealtime[] terminalData = restTemplate.getForEntity(url, TerminalRealtime[].class).getBody();
            if (terminalData != null && terminalData.length > 0) {
                return terminalData[0];
            }
            return null;
        } catch (Exception e) {
            log.error("获取数据服务节点[" + serverNode + "]实时数据异常", e);
            throw new BusinessException("获取实时数据失败");
        }
    }

    /**
     * 获取节点下多个终端实时数据
     */
    public List<TerminalRealtime> getRealTimeData(String serverNode, String terminalIds, boolean judgePermission) {
        if (serverNode == null || serverNode.length() == 0) {
            throw new BusinessException("请选择一个数据服务节点");
        }

        if (terminalIds == null) {
            terminalIds = "";
        }

        try {
            String url = "http://msms-data-" + serverNode + "/terminal/data/realtime?terminalIds=" + terminalIds;
            TerminalRealtime[] terminalData = restTemplate.getForEntity(url, TerminalRealtime[].class).getBody();
            List<TerminalRealtime> result = new ArrayList<>(terminalData == null ? 0 : terminalData.length);
            if (terminalData != null && terminalData.length > 0) {
                if (judgePermission) {
                    MonitorUserSession userSession = MonitorUserSession.getCurrentUserSession();
                    for (TerminalRealtime tr : terminalData) {
                        if (DataPermissionUtil.hasStationPermission(userSession, terminalContainer.getTerminal(tr.getId()))) {
                            result.add(tr);
                        }
                    }
                } else {
                    for (TerminalRealtime tr : terminalData) {
                        result.add(tr);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error("获取数据服务节点[" + serverNode + "]实时数据异常", e);
            throw new BusinessException("获取实时数据失败");
        }
    }


    /**
     * 获取节点下所有报警信息
     */
    public List<TerminalAlarms> getAlarms(String serverNode, boolean judgePermission) {
        if (serverNode == null || serverNode.length() == 0) {
            throw new BusinessException("请选择一个数据服务节点");
        }
        try {
            String url = "http://msms-data-" + serverNode + "/terminal/data/alarm";
            TerminalAlarms[] terminalAlarms = restTemplate.getForEntity(url, TerminalAlarms[].class).getBody();
            List<TerminalAlarms> result = new ArrayList<>(terminalAlarms == null ? 0 : terminalAlarms.length);
            if (terminalAlarms != null && terminalAlarms.length > 0) {
                if (judgePermission) {
                    MonitorUserSession userSession = MonitorUserSession.getCurrentUserSession();
                    for (TerminalAlarms ta : terminalAlarms) {
                        if (DataPermissionUtil.hasStationPermission(userSession, terminalContainer.getTerminal(ta.getId()))) {
                            result.add(ta);
                            for (AlarmStatus as : ta.getAlarms()) {
                                as.setName(alarmContainer.getAlarmName(as.getId()));
                            }
                        }
                    }
                } else {
                    for (TerminalAlarms ta : terminalAlarms) {
                        result.add(ta);
                        for (AlarmStatus as : ta.getAlarms()) {
                            as.setName(alarmContainer.getAlarmName(as.getId()));
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error("获取数据服务节点[" + serverNode + "]实时报警数据异常", e);
            throw new BusinessException("获取实时报警数据失败");
        }
    }

    /**
     * 获取某终端所有报警信息
     */
    public TerminalAlarms getTerminalAlarms(int terminalId) {
        CTerminal terminal = terminalContainer.getTerminal(terminalId);
        if (terminal == null || !terminal.isEnabled()) {
            throw new BusinessException("终端不存在或未被启用");
        }

        if (!DataPermissionUtil.hasStationPermission(MonitorUserSession.getCurrentUserSession(), terminal)) {
            throw new BusinessException("没有权限获取数据");
        }

        String serverNode = terminal.getNodeCode();
        try {
            String url = "http://msms-data-" + serverNode + "/terminal/data/alarm/terminal?terminalId=" + terminalId;
            TerminalAlarms ta = restTemplate.getForEntity(url, TerminalAlarms.class).getBody();
            if (ta != null) {
                for (AlarmStatus as : ta.getAlarms()) {
                    as.setName(alarmContainer.getAlarmName(as.getId()));
                }
                return ta;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("获取终端[" + terminalId + "]实时报警数据异常", e);
            throw new BusinessException("获取实时报警数据失败");
        }
    }

    /**
     * 获取终端列表及其实时数据
     */
    public PageResult<TerminalSimpleRealtime> findSimpleTerminalListRealTime(StationDeviceQuery query) {
        // 查找可用的终端
        query.setEnabled(true);
        PageResult pageResult = stationDeviceService.findStationDeviceIdPage(query);
        List deviceIds = pageResult.getData();
        if (deviceIds != null && deviceIds.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object deviceId : deviceIds) {
                sb.append(deviceId).append(',');
            }

            String serverNode = query.getServerNode();
            if (serverNode == null || serverNode.length() == 0) {
                throw new BusinessException("请选择一个数据服务节点");
            }

            try {
                String url = "http://msms-data-" + serverNode + "/terminal/data/realtime/simple?terminalIds=" + sb.toString();
                TerminalSimpleRealtime[] terminalData = restTemplate.getForEntity(url, TerminalSimpleRealtime[].class).getBody();
                if (terminalData != null) {
                    pageResult.setData(Arrays.asList(terminalData));
                } else {
                    pageResult.setData(new ArrayList());
                }
            } catch (Exception e) {
                log.error("获取数据服务节点[" + serverNode + "]实时数据异常", e);
                throw new BusinessException("获取实时数据失败");
            }
        }
        return pageResult;
    }

    /**
     * 获取终端列表及其实时数据
     */
    public TerminalListRealtime findTerminalListRealTime(StationDeviceQuery query) {
        // 查找可用的终端
        query.setEnabled(true);
        PageResult<StationDeviceMonitorVO> pageResult = stationDeviceService.findStationDevicePage(query);
        List<StationDeviceMonitorVO> devices = pageResult.getData();
        List<TerminalRealtime> data = null;
        if (devices != null && devices.size() > 0) {
            String ids = "";
            for (StationDeviceMonitorVO device : devices) {
                ids += device.getId() + ",";
            }
            data = getRealTimeData(query.getServerNode(), ids, false);
        }
        return new TerminalListRealtime(pageResult, variableContainer.getShowVariables(), data);
    }

    /**
     * 获取终端详细和实时数据
     */
    public TerminalDetailRealtime findTerminalDetailRealtime(int terminalId) {
        CTerminal terminal = terminalContainer.getTerminal(terminalId);
        if (terminal == null || !terminal.isEnabled()) {
            throw new BusinessException("终端不存在或未被启用");
        }

        TerminalRealtime data = getRealTimeData(terminalId);

        TerminalDetailRealtime detail = new TerminalDetailRealtime();
        detail.setId(terminal.getId());
        detail.setStationId(terminal.getStationId());
        detail.setStationName(terminal.getStationName());
        detail.setType(terminal.getType());
        detail.setUid(terminal.getUid());
        detail.setData(data);
        detail.setVariables(variableContainer.getShowVariableOfTerminal(terminal));

        return detail;
    }

    /**
     * 获取终端历史数据
     */
    public String getTerminalHistoryData(TerminalDataQuery query) {
        int terminalId = query.getTerminalId();
        CTerminal cTerminal = terminalContainer.getTerminal(terminalId);

        // 历史数据不必须终端启用时才能查询
        if (cTerminal == null) {
            throw new BusinessException("对应终端不存在");
        }

        if (!DataPermissionUtil.hasStationPermission(MonitorUserSession.getCurrentUserSession(), cTerminal)) {
            throw new BusinessException("没有权限获取数据");
        }

        Date startDate = query.getStartDate();
        Date endDate = query.getEndDate();

        if (startDate == null) {
            if (endDate == null) {
                endDate = new Date();
            }
            startDate = TimeUtil.getDateBefore(endDate.getTime(), terminalHistoryDataDefaultTimeSpan);
        } else if (endDate == null) {
            endDate = new Date();
        }

        long intervalDays = TimeUtil.getIntervalDays(startDate.getTime(), endDate.getTime());
        if (intervalDays > terminalHistoryDataMaxTimeSpan) {
            throw new BusinessException("历史数据时间跨度不能超过" + terminalHistoryDataMaxTimeSpan + "天");
        }

        query.setStartDate(startDate);
        query.setEndDate(endDate);

        // 变量检测
        ConfigTerminal terminal = stationDeviceService.get(terminalId);

        List<Integer> variableIds = DataUtil.getShowVariableId(terminal.getVariableIds(), variableContainer.getShowVariables(), query.getVariableIds());

        if (variableIds.size() == 0) {
            throw new BusinessException("请选择查看的历史数据变量");
        }

        query.setVariableIds(variableIds);

        try {
            String url = "http://msms-data-" + cTerminal.getNodeCode() + "/terminal/data/history";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TerminalDataQuery> entity = new HttpEntity<>(query, headers);
            return restTemplate.postForEntity(url, entity, String.class).getBody();
        } catch (Exception e) {
            log.error("获取终端[" + terminalId + "]历史数据异常", e);
            throw new BusinessException("获取终端历史数据失败");
        }
    }


}
