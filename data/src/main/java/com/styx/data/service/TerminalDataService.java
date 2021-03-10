package com.styx.data.service;

import com.styx.common.api.R;
import com.styx.common.config.GlobalUtils;
import com.styx.common.exception.BusinessException;
import com.styx.common.utils.StringUtil;
import com.styx.common.utils.TimeUtil;
import com.styx.data.core.Constants;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalAlarmHandler;
import com.styx.data.core.terminal.TerminalDataInitializer;
import com.styx.data.core.terminal.Variable;
import com.styx.data.mapper.*;
import com.styx.data.model.*;
import com.styx.data.service.dto.Data4Upload;
import com.styx.data.service.dto.DataRecord;
import com.styx.data.service.dto.MoveTerminalInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author TontoZhou
 * @since 2020/10/27
 */
@Slf4j
@Service
public class TerminalDataService implements TerminalAlarmHandler, TerminalDataInitializer {

    @Autowired
    private TerminalInfoMapper terminalInfoMapper;

    @Autowired
    private TerminalDataMapper terminalDataMapper;

    @Autowired
    private TerminalDataDetailMapper terminalDataDetailMapper;

    @Autowired
    private TerminalAlarmHistoryMapper terminalAlarmHistoryMapper;

    @Autowired
    private TerminalAlarmMapper terminalAlarmMapper;

    @Autowired
    private InternalMonitorService monitorService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 持久化终端数据
     */
    public void persistData(Collection<Terminal> terminals) {
        Date now = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(now);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int date = year * 10000 + month * 100 + day;

        for (Terminal terminal : terminals) {
            int tid = terminal.getId();
            try {
                long _lastLoginTime = terminal.getLastLoginTime();
                Date lastLoginTime = _lastLoginTime > 0 ? new Date(_lastLoginTime) : null;

                int workTotalMinutes = (int) (terminal.getWorkTotalTime() / 60000);
                long maintainOffTime = terminal.getMaintainOffTime();
                if (terminalInfoMapper.updateInfo(tid, lastLoginTime, workTotalMinutes, maintainOffTime) == 0) {
                    TerminalInfo terminalInfo = new TerminalInfo();
                    terminalInfo.setId(terminal.getId());
                    terminalInfo.setLastLoginTime(lastLoginTime);
                    terminalInfo.setWorkTotalTime(workTotalMinutes);
                    terminalInfo.setUpdateTime(now);
                    terminalInfo.setMaintainOffTime(maintainOffTime);
                    terminalInfoMapper.insert(terminalInfo);
                }

                boolean isOnline = terminal.isOnline();
                int workStatus = terminal.getWorkStatus();

                TerminalData terminalData = new TerminalData();
                terminalData.setIsOnline(isOnline);
                terminalData.setTerminalId(tid);
                terminalData.setWorkStatus(workStatus);
                terminalData.setCreateTime(now);
                terminalData.setDay(date);
                terminalData.setHour(hour);
                terminalData.setIsTest(terminal.isTest());
                terminalData.setIsSelf(true);

                terminalDataMapper.insert(terminalData);

                // 只有在线并且工作状态为处理中和排放中才持久化变量数据
                if (isOnline && (workStatus == Constants.TERMINAL_WORK_STATUS_PROCESS
                        || workStatus == Constants.TERMINAL_WORK_STATUS_DISCHARGE)) {
                    Map<Integer, Float> varValueMap = terminal.getVariableValueMap();
                    if (varValueMap != null && varValueMap.size() > 0) {
                        Long dataId = terminalData.getId();
                        List<TerminalDataDetail> detailList = new ArrayList<>();

                        for (Variable variable : terminal.getPersistedVariables()) {
                            int vid = variable.getId();
                            Float value = varValueMap.get(vid);
                            if (value != null) {
                                detailList.add(new TerminalDataDetail(dataId, vid, value));
                            }
                        }

                        terminalDataDetailMapper.insertBatch(detailList);
                    }
                }

                // 处理报警

            } catch (Exception e) {
                log.error("终端[ID:" + tid + "]持久化数据异常", e);
            }
        }
    }


    @Override
    public void alarmTriggerHandle(int terminalId, int alarmId, long startTime) {
        try {
            TerminalAlarm record = new TerminalAlarm();
            record.setAlarmId(alarmId);
            record.setTerminalId(terminalId);
            record.setStartTime(new Date(startTime));
            terminalAlarmMapper.insert(record);
            //触发报警
            monitorService.triggerAlarm(record);
        } catch (Exception e) {
            // 如果是重复记录异常，可以忽略
            log.error("新增报警[terminalID:" + terminalId + ", alarmID:" + alarmId + "]记录异常,", e);
        }
    }

    @Override
    @Transactional
    public void alarmClosedHandle(int terminalId, int alarmId, long startTime) {
        // 报警正常关闭，删除原报警状态并增加一条报警历史记录，同时上传到monitor微服务上

        Date _startTime = new Date(startTime);

        terminalAlarmMapper.deleteAlarm(terminalId, alarmId);

        TerminalAlarmHistory history = new TerminalAlarmHistory();
        history.setTerminalId(terminalId);
        history.setAlarmId(alarmId);
        history.setStartTime(_startTime);
        history.setEndTime(new Date());
        history.setClosed(true);

        try {
            R result = monitorService.closeAlarm(history);
            history.setUploaded(result.isSuccess());
        } catch (Exception e) {
            history.setUploaded(true);
        }

        terminalAlarmHistoryMapper.insert(history);
    }

    @Override
    @Transactional
    public void alarmUntreatedHandle(int terminalId, int alarmId, long startTime) {
        Date _startTime = new Date(startTime);

        TerminalAlarmHistory history = new TerminalAlarmHistory();
        history.setTerminalId(terminalId);
        history.setAlarmId(alarmId);
        history.setStartTime(_startTime);
        history.setEndTime(new Date());
        history.setClosed(false);

        try {
            R result = monitorService.untreatedAlarm(history);
            history.setUploaded(result.isSuccess());
        } catch (Exception e) {
            history.setUploaded(true);
        }

        terminalAlarmHistoryMapper.insert(history);
    }

    @Override
    public Map<Integer, Terminal.AlarmStatus> getAlarmTriggering(int id) {
        List<TerminalAlarm> alarmList = terminalAlarmMapper.getAlarmIdOfTerminal(id);
        if (alarmList != null && alarmList.size() > 0) {
            Map<Integer, Terminal.AlarmStatus> map = new HashMap<>();
            for (TerminalAlarm terminalAlarm : alarmList) {
                long time = terminalAlarm.getStartTime().getTime();
                int aid = terminalAlarm.getAlarmId();
                map.put(aid, new Terminal.AlarmStatus(aid, time, time));
            }
            return map;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public TerminalInfo getTerminalInfo(int id) {
        return terminalInfoMapper.selectById(id);
    }

    /**
     * 查找终端数据
     */
    public List<DataRecord> findTerminalData(int terminalId, Date startDate, Date endDate, List<Integer> variableIds) {
        int startDay = TimeUtil.getSerialNumberByDay(startDate);
        int endDay = TimeUtil.getSerialNumberByDay(endDate);
        return terminalDataMapper.findTerminalData(terminalId, startDay, endDay, variableIds);
    }

    /**
     * 删除测试终端数据
     */
    public void deleteTestData(List<Integer> terminalIds) {
        if (terminalIds.size() > 0) {
            terminalDataMapper.deleteTestData(terminalIds);
        }
    }

    /**
     * 接收子节点上传的数据
     */
    @Transactional
    public void receiveUploadData(List<Data4Upload> dataList) {
        if (dataList != null && dataList.size() > 0) {
            List<Data4Upload> emptyList = new ArrayList<>();
            List<TerminalDataDetail> detailList = new ArrayList<>(dataList.size());
            for (Data4Upload data : dataList) {
                String valueString = data.getValues();
                if (valueString == null || valueString.length() == 0) {
                    emptyList.add(data);
                } else {
                    TerminalData terminalData = new TerminalData();
                    terminalData.setIsOnline(data.getIsOnline());
                    terminalData.setTerminalId(data.getTerminalId());
                    terminalData.setWorkStatus(data.getWorkStatus());
                    terminalData.setCreateTime(data.getCreateTime());
                    terminalData.setDay(data.getDay());
                    terminalData.setHour(data.getHour());
                    terminalData.setIsTest(false);
                    terminalData.setIsSelf(false);

                    terminalDataMapper.insert(terminalData);

                    for (String varValueStr : valueString.split(",")) {
                        String[] arr = varValueStr.split(":");

                        long dataId = terminalData.getId();
                        int varId = Integer.valueOf(arr[0]);
                        float value = Float.valueOf(arr[1]);

                        detailList.add(new TerminalDataDetail(dataId, varId, value));
                    }
                }
            }

            if (emptyList.size() > 0) {
                terminalDataMapper.insertUploadDataBatch(emptyList);
            }

            if (detailList.size() > 0) {
                terminalDataDetailMapper.insertBatch(detailList);
            }

        }
    }

    public void moveToServerNode(String targetNode, int[] terminalIds) {
        if (targetNode == null || targetNode.length() == 0) {
            throw new BusinessException("转移目标节点不能为空");
        }

        if (terminalIds == null || terminalIds.length == 0) return;

        List<MoveTerminalInfo> data = new ArrayList<>(terminalIds.length);

        for (int terminalId : terminalIds) {
            List<TerminalAlarm> alarms = terminalAlarmMapper.getAlarmIdOfTerminal(terminalId);
            TerminalInfo info = terminalInfoMapper.selectById(terminalId);

            if ((alarms == null || alarms.size() == 0) && info == null) {
                continue;
            }

            data.add(new MoveTerminalInfo(terminalId, alarms, info));
        }

        if (data.size() == 0) return;

        try {
            String url = GlobalUtils.getDataServiceURI(targetNode, "/terminal/control/come");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<MoveTerminalInfo>> entity = new HttpEntity<>(data, headers);
            restTemplate.postForEntity(url, entity, R.class);
        } catch (Exception e) {
            log.error("转移终端" + StringUtil.toString(terminalIds) + "信息数据异常", e);
            throw new BusinessException("转移终端信息数据异常");
        }
    }

    @Transactional
    public void saveMovedTerminalInfo(List<MoveTerminalInfo> data) {
        for (MoveTerminalInfo item : data) {
            int terminalId = item.getTerminalId();
            // 删除终端所有报警和信息
            terminalAlarmMapper.deleteAlarmByTerminal(terminalId);
            terminalInfoMapper.selectById(terminalId);

            List<TerminalAlarm> terminalAlarms = item.getTerminalAlarms();
            if (terminalAlarms != null) {
                for (TerminalAlarm ta : terminalAlarms) {
                    terminalAlarmMapper.insert(ta);
                }
            }

            TerminalInfo info = item.getTerminalInfo();
            if (info != null) {
                terminalInfoMapper.insert(info);
            }
        }
    }


}
