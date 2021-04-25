package com.styx.data.service;

import com.styx.common.exception.BusinessException;
import com.styx.common.utils.TimeUtil;
import com.styx.data.core.terminal.*;
import com.styx.data.mapper.TerminalDataDetailMapper;
import com.styx.data.mapper.TerminalDataMapper;
import com.styx.data.mapper.TerminalInfoMapper;
import com.styx.data.model.TerminalData;
import com.styx.data.model.TerminalDataDetail;
import com.styx.data.model.TerminalInfo;
import com.styx.data.service.dto.DataRecord;
import com.styx.data.service.dto.HistoryDataQuery;
import com.styx.data.service.vo.TerminalAlarms;
import com.styx.data.service.vo.TerminalRealData;
import com.styx.data.service.vo.TerminalSimpleRealData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author TontoZhou
 * @since 2020/10/27
 */
@Slf4j
@Service
public class TerminalDataService {

    @Autowired
    private TerminalInfoMapper terminalInfoMapper;

    @Autowired
    private TerminalDataMapper terminalDataMapper;

    @Autowired
    private TerminalDataDetailMapper terminalDataDetailMapper;

    @Autowired
    private TerminalManager terminalManager;

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
                if (terminalInfoMapper.updateInfo(tid, lastLoginTime, workTotalMinutes) == 0) {
                    TerminalInfo terminalInfo = new TerminalInfo();
                    terminalInfo.setId(terminal.getId());
                    terminalInfo.setLastLoginTime(lastLoginTime);
                    terminalInfo.setWorkTotalTime(workTotalMinutes);
                    terminalInfo.setUpdateTime(now);
                    terminalInfoMapper.insert(terminalInfo);
                }

                boolean isOnline = terminal.isOnline();

                TerminalData terminalData = new TerminalData();
                terminalData.setIsOnline(isOnline);
                terminalData.setTerminalId(tid);
                terminalData.setCreateTime(now);
                terminalData.setDay(date);
                terminalData.setHour(hour);

                terminalDataMapper.insert(terminalData);

                // 只有在线并且工作状态为处理中和排放中才持久化变量数据
                if (isOnline) {
                    Map<Integer, Float> varValueMap = terminal.getVariableValueMap();
                    if (varValueMap != null && varValueMap.size() > 0) {
                        Long dataId = terminalData.getId();
                        List<TerminalDataDetail> detailList = new ArrayList<>();

                        for (Variable variable : terminal.getVariables()) {
                            if (variable.isPersisted()) {
                                int vid = variable.getId();
                                Float value = varValueMap.get(vid);
                                if (value != null) {
                                    detailList.add(new TerminalDataDetail(dataId, vid, value));
                                }
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


    /**
     * 获取指定终端简单的实时数据
     */
    public List<TerminalSimpleRealData> getSimpleRealData(Set<Integer> terminalIds) {
        if (terminalIds != null && terminalIds.size() > 0) {
            TerminalContainer terminalContainer = terminalManager.getTerminalContainer();
            if (terminalContainer != null) {
                List<TerminalSimpleRealData> data = new ArrayList<>(terminalIds.size());
                for (Integer terminalId : terminalIds) {
                    Terminal terminal = terminalContainer.getTerminal(terminalId);
                    if (terminal != null) {
                        data.add(getSimpleRealData(terminal));
                    }
                }
                return data;
            }
        }
        return Collections.emptyList();
    }

    /**
     * 获取所有终端简单的实时数据
     */
    public List<TerminalSimpleRealData> getSimpleRealData() {
        TerminalContainer terminalContainer = terminalManager.getTerminalContainer();
        if (terminalContainer != null) {
            List<Terminal> terminals = terminalContainer.getTerminals();
            List<TerminalSimpleRealData> data = new ArrayList<>(terminals.size());
            for (Terminal terminal : terminals) {
                data.add(getSimpleRealData(terminal));
            }
            return data;
        }
        return Collections.emptyList();
    }


    private TerminalSimpleRealData getSimpleRealData(Terminal terminal) {
        TerminalSimpleRealData data = new TerminalSimpleRealData();
        data.setId(terminal.getId());
        boolean isOnline = terminal.isOnline();
        data.setOnline(isOnline);
        if (isOnline && !terminal.getAlarmTriggeringList().isEmpty()) {
            data.setAlarmTriggering(true);
        }
        return data;
    }

    /**
     * 获取指定终端纤细数据
     */
    public TerminalRealData getDetailRealData(int terminalId) {
        TerminalContainer terminalContainer = terminalManager.getTerminalContainer();
        if (terminalContainer != null) {
            Terminal terminal = terminalContainer.getTerminal(terminalId);
            if (terminal != null) {
                return getDetailRealData(terminal);
            }
        }
        return null;
    }


    /**
     * 获取实时数据对象
     */
    private TerminalRealData getDetailRealData(Terminal terminal) {
        TerminalRealData data = new TerminalRealData();
        data.setId(terminal.getId());

        boolean isOnline = terminal.isOnline();
        data.setOnline(isOnline);
        data.setLastLoginTime(terminal.getLastLoginTime());
        data.setLastWorkTime(terminal.getDataUpdateTime());

        int workTotalMinutes = (int) (terminal.getWorkTotalTime() / 60000);
        data.setWorkTotalTime(workTotalMinutes);

        data.setVariableValues(isOnline ? terminal.getVariableValueMap() : null);
        data.setAlarmStatuses(terminal.getAlarmTriggeringList());

        return data;
    }

    /**
     * 查找终端历史数据
     */
    public List<DataRecord> findHistoryData(HistoryDataQuery query) {
        int terminalId = query.getTerminalId();
        Date startDate = query.getStartDate();
        Date endDate = query.getEndDate();

        if (TimeUtil.getIntervalDays(startDate.getTime(), endDate.getTime()) >= 31) {
            throw new BusinessException("历史数据查询间隔不能超过31天");
        }

        int startDay = TimeUtil.getSerialNumberByDay(startDate);
        int endDay = TimeUtil.getSerialNumberByDay(endDate);
        return terminalDataMapper.findTerminalData(terminalId, startDay, endDay, query.getVariableIds());
    }


    public List<TerminalAlarms> getAlarmStatuses() {
        TerminalContainer terminalContainer = terminalManager.getTerminalContainer();
        if (terminalContainer != null) {
            List<Terminal> terminals = terminalContainer.getTerminals();
            List<TerminalAlarms> data = new ArrayList<>(terminals.size() / 2 + 1);
            for (Terminal terminal : terminals) {
                List<AlarmStatus> alarmStatuses = terminal.getAlarmTriggeringList();
                if (!alarmStatuses.isEmpty())
                    data.add(new TerminalAlarms(terminal.getId(), alarmStatuses));
            }
            return data;
        }
        return Collections.emptyList();
    }

    public List<AlarmStatus> getAlarmStatuses(int terminalId) {
        TerminalContainer terminalContainer = terminalManager.getTerminalContainer();
        if (terminalContainer != null) {
            Terminal terminal = terminalContainer.getTerminal(terminalId);
            if (terminal != null) {
                return terminal.getAlarmTriggeringList();
            }
        }
        return null;
    }
}
