package com.styx.data.service;

import com.styx.common.utils.TimeUtil;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.Variable;
import com.styx.data.mapper.TerminalDataDetailMapper;
import com.styx.data.mapper.TerminalDataMapper;
import com.styx.data.mapper.TerminalInfoMapper;
import com.styx.data.model.TerminalData;
import com.styx.data.model.TerminalDataDetail;
import com.styx.data.model.TerminalInfo;
import com.styx.data.service.dto.DataRecord;
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



    /**
     * 查找终端数据
     */
    public List<DataRecord> findTerminalData(int terminalId, Date startDate, Date endDate, List<Integer> variableIds) {
        int startDay = TimeUtil.getSerialNumberByDay(startDate);
        int endDay = TimeUtil.getSerialNumberByDay(endDate);
        return terminalDataMapper.findTerminalData(terminalId, startDay, endDay, variableIds);
    }



}
