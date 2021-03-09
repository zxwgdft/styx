package com.styx.data.service;

import com.styx.common.api.R;
import com.styx.common.exception.BusinessException;
import com.styx.common.utils.TimeUtil;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalListener;
import com.styx.data.core.terminal.TerminalManager;
import com.styx.data.core.terminal.Variable;
import com.styx.data.mapper.*;
import com.styx.data.model.DataAnalysisDay;
import com.styx.data.model.DataAnalysisHour;
import com.styx.data.model.TerminalDataFlow;
import com.styx.data.service.vo.VariablePassStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TontoZhou
 * @since 2020/11/18
 */
@Slf4j
@Component
public class DataAnalysisService implements TerminalListener {

    @Autowired
    private DataAnalysisDayMapper dataAnalysisDayMapper;

    @Autowired
    private DataAnalysisHourMapper dataAnalysisHourMapper;

    @Autowired
    private TerminalStatisticsMapper terminalStatisticsMapper;

    @Autowired
    private TerminalDataFlowMapper terminalDataFlowMapper;

    @Autowired
    private TerminalDataMapper terminalDataMapper;

    @Autowired
    private TerminalManager terminalManager;

    @Autowired
    private InternalMonitorService internalMonitorService;

    @Value("${data.analysis.day.upload-max:5000}")
    private int analysisDayUploadMax;

    @Value("${data.analysis.hour.upload-max:5000}")
    private int analysisHourUploadMax;

    @Value("${data.protocol.variable.id-ljll}")
    private int variable_id_ljll;

    private int uploadPerTime = 500;

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

    /**
     * 月报数据统计
     * 每天凌晨1点统计
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void addDataAnalysisDay() {
        Date yesterday = TimeUtil.getYesterday();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(yesterday);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int date = year * 10000 + month * 100 + day;

        addDataAnalysisDay(date);
    }


    public void addDataAnalysisDay(int date) {

        int effected = dataAnalysisDayMapper.analyzeAndInsert(date);

        if (log.isDebugEnabled()) {
            log.debug("统计时段[" + date + "]的月报数据，共插入" + effected + "条记录");
        }

        int needUpload = dataAnalysisDayMapper.countForUpload();
        // 分批上传
        int i = 0;
        for (; i < analysisDayUploadMax && i < needUpload; ) {
            List<DataAnalysisDay> data = dataAnalysisDayMapper.findDataForUpload(uploadPerTime);
            int size = data == null ? 0 : data.size();
            if (size > 0) {
                try {
                    R response = internalMonitorService.uploadDataAnalysisDay(data);
                    if (response.isSuccess()) {
                        dataAnalysisDayMapper.updateUploaded(size);
                    } else {
                        log.error("上传月报数据失败");
                        break;
                    }
                } catch (Exception e) {
                    log.error("上传月报数据异常", e);
                    break;
                }

                i += size;

                if (size < uploadPerTime) {
                    break;
                }
            } else {
                break;
            }
        }

        log.info("成功上传月报数据" + i + "条");
    }

    /**
     * 日报数据统计
     * 每小时过2分钟统计
     */
    @Scheduled(cron = "0 2 * * * ?")
    public void addDataAnalysisHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int date = year * 10000 + month * 100 + day;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        int effected = dataAnalysisHourMapper.analyzeAndInsert(date, hour);

        if (log.isDebugEnabled()) {
            log.debug("统计时段[" + date + "/" + hour + "]的日报数据，共插入" + effected + "条记录");
        }


        int needUpload = dataAnalysisHourMapper.countForUpload();
        // 分批上传
        int i = 0;
        for (; i < analysisDayUploadMax && i < needUpload; ) {
            List<DataAnalysisHour> data = dataAnalysisHourMapper.findDataForUpload(uploadPerTime);
            int size = data == null ? 0 : data.size();
            if (size > 0) {
                try {
                    R response = internalMonitorService.uploadDataAnalysisHour(data);
                    if (response.isSuccess()) {
                        dataAnalysisHourMapper.updateUploaded(size);
                    } else {
                        log.error("上传日报数据失败");
                        break;
                    }
                } catch (Exception e) {
                    log.error("上传日报数据异常", e);
                    break;
                }

                i += size;

                if (size < uploadPerTime) {
                    break;
                }
            } else {
                break;
            }
        }

        log.info("成功上传日报数据" + i + "条");


        // 每小时持久化一次累计流量，可能会造成终端离线情况下某时间段内1小时的数据差，可接受
        persistTotalFlowData();
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

    /**
     * 获取终端变量数据合格情况
     */
    public List<VariablePassStatus> getVariablePassStatusOfTerminal(int terminalId, int startDay, int endDay, List<Integer> variableIds) {
        int d = endDay - startDay;
        if (d > 31 || d < 0) {
            throw new BusinessException("查询数据跨度不能超过一个月");
        }

        if (variableIds == null) {
            return new ArrayList<>();
        }

        List<VariablePassStatus> result = new ArrayList<>(variableIds.size());
        Map<Integer, Variable> variableMap = terminalManager.getVariableMap();

        for (Integer variableId : variableIds) {
            Variable variable = variableMap.get(variableId);
            if (variable != null) {
                Float max = variable.getMax();
                Float min = variable.getMin();
                if (max != null && min != null) {
                    VariablePassStatus status = terminalStatisticsMapper.getVariablePassNum(terminalId, variableId, startDay, endDay, max, min);
                    status.setVariableId(variableId);
                    status.setVariableName(variable.getName());
                    if (status.getPassNum() == null) {
                        status.setPassNum(0);
                    }
                    result.add(status);
                }
            }
        }

        return result;
    }
}
