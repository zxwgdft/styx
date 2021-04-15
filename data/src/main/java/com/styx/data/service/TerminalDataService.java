package com.styx.data.service;

import com.styx.common.utils.TimeUtil;
import com.styx.data.core.Constants;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalAlarmHandler;
import com.styx.data.core.terminal.TerminalDataInitializer;
import com.styx.data.core.terminal.Variable;
import com.styx.data.mapper.TerminalAlarmMapper;
import com.styx.data.mapper.TerminalDataDetailMapper;
import com.styx.data.mapper.TerminalDataMapper;
import com.styx.data.mapper.TerminalInfoMapper;
import com.styx.data.model.TerminalAlarm;
import com.styx.data.model.TerminalData;
import com.styx.data.model.TerminalDataDetail;
import com.styx.data.model.TerminalInfo;
import com.styx.data.service.dto.Data4Upload;
import com.styx.data.service.dto.DataRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private TerminalAlarmMapper terminalAlarmMapper;

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
            //TODO 触发报警
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
        //TODO
    }

    @Override
    @Transactional
    public void alarmUntreatedHandle(int terminalId, int alarmId, long startTime) {
        Date _startTime = new Date(startTime);
        // TODO
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



}
