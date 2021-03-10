package com.styx.data.service;

import com.styx.common.api.R;
import com.styx.common.config.GlobalUtils;
import com.styx.data.core.terminal.TerminalManager;
import com.styx.data.mapper.SysMapMapper;
import com.styx.data.mapper.TerminalAlarmHistoryMapper;
import com.styx.data.mapper.TerminalDataMapper;
import com.styx.data.model.TerminalAlarmHistory;
import com.styx.data.service.dto.Data4Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author TontoZhou
 * @since 2020/11/24
 */
@Slf4j
@Service
public class TerminalDataUploadService implements ApplicationRunner, Runnable {

    @Autowired
    private TerminalAlarmHistoryMapper terminalAlarmHistoryMapper;

    @Autowired
    private InternalMonitorService monitorService;

    @Autowired
    private SysMapMapper sysMapMapper;

    @Autowired
    private TerminalDataMapper terminalDataMapper;

    @Autowired
    private TerminalManager terminalManager;

    @Autowired
    private RestTemplate restTemplate;

    private Integer dataUploadIndex = null;

    @Value("${data.upload.per-max:1000}")
    private int uploadDataPerMax;
    @Value("${data.upload.interval:10}")
    private int uploadDataInterval;
    private int uploadPerTime = 200;

    @Override
    public void run() {

        // 每次执行最多尝试上传500条（该数据并不会太多，这里不设想大量数据情况）
        List<TerminalAlarmHistory> list = terminalAlarmHistoryMapper.findNotUploaded();
        if (list != null && list.size() > 0) {
            try {
                for (TerminalAlarmHistory history : list) {
                    R result = monitorService.uploadAlarmHistory(history);
                    if (result.isSuccess()) {
                        terminalAlarmHistoryMapper.updateUploaded(history.getId());
                    }
                }
            } catch (Exception e) {
                log.error("上传报警历史异常", e);
            }
        }

        String parentNodeCode = terminalManager.getParentNodeCode();

        if (parentNodeCode != null && parentNodeCode.length() > 0 && dataUploadIndex != null) {
            int count = 0;
            for (; ; ) {
                List<Data4Upload> dataList = terminalDataMapper.findTerminalData4Upload(dataUploadIndex, uploadPerTime);
                if (dataList != null) {
                    int size = dataList.size();
                    if (size > 0) {
                        try {
                            String url = GlobalUtils.getDataServiceURI(parentNodeCode, "/terminal/data/upload");
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            HttpEntity<List<Data4Upload>> entity = new HttpEntity<>(dataList, headers);
                            restTemplate.postForEntity(url, entity, R.class);
                        } catch (Exception e) {
                            log.error("上传数据到父节点[" + parentNodeCode + "]异常", e);
                            break;
                        }

                        dataUploadIndex = dataList.get(size - 1).getId();
                        sysMapMapper.updateDataUploadIndex(String.valueOf(dataUploadIndex));

                        count += size;
                        if (size == uploadPerTime && count < uploadDataPerMax) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                            }
                            continue;
                        }
                    }
                }
                break;
            }

            log.info("上传了[" + count + "]条数据到父节点[" + parentNodeCode + "]");
        }
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

        String index = sysMapMapper.getDataUploadIndex();
        if (index == null || index.length() == 0) {
            index = "0";
            // 插入初始值
            sysMapMapper.insertDataUploadIndex(index);
        }
        dataUploadIndex = Integer.valueOf(index);

        ScheduledExecutorService service1 = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("uploadData");
                return thread;
            }
        });

        service1.scheduleWithFixedDelay(this, 1, uploadDataInterval, TimeUnit.MINUTES);

    }
}
