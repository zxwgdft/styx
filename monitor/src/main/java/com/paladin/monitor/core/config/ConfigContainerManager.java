package com.paladin.monitor.core.config;

import com.paladin.framework.spring.SpringBeanHelper;
import com.paladin.framework.spring.SpringContainer;
import com.paladin.monitor.mapper.sys.SysVersionMapper;
import com.styx.common.spring.SpringBeanHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author TontoZhou
 * @since 2020/10/30
 */
@Slf4j
@Component
public class ConfigContainerManager implements ApplicationRunner, Runnable {

    @Value("${monitor.config.check-version-interval:2}")
    private int checkVersionInterval;

    @Autowired
    private SysVersionMapper sysVersionMapper;

    private Map<String, ConfigContainer> containerMap;

    public void run(ApplicationArguments args) throws Exception {
        Map<String, ConfigContainer> configContainerMap = SpringBeanHelper.getBeansByType(ConfigContainer.class);

        Map<String, ConfigContainer> containerMap = new HashMap<>();
        for (Map.Entry<String, ConfigContainer> entry : configContainerMap.entrySet()) {
            ConfigContainer configContainer = entry.getValue();
            String containerId = configContainer.getId();

            long version = sysVersionMapper.getVersion(containerId);
            configContainer.setVersion(version);
            configContainer.load();

            containerMap.put(containerId, configContainer);
        }

        this.containerMap = Collections.unmodifiableMap(containerMap);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("checkConfigVersion");
                return thread;
            }
        });

        service.scheduleWithFixedDelay(this, 10, checkVersionInterval, TimeUnit.SECONDS);

        log.debug("开启配置容器版本检测线程");

    }


    // 检查数据容器版本变化
    public void run() {
        for (ConfigContainer container : containerMap.values()) {
            try {
                long version = sysVersionMapper.getVersion(container.getId());
                long curVersion = container.getVersion();
                if (version > curVersion) {
                    container.reload();
                    container.setVersion(version);
                } else if (version < curVersion) {
                    sysVersionMapper.updateVersion(container.getId(), curVersion);
                }
            } catch (Exception e) {
                log.error("数据容器[" + container.getId() + "]重读异常", e);
            }
        }

        //log.debug("进行一次数据容器版本检查");
    }


    private void reloadContainer(ConfigContainer container) {
        if (container != null) {
            container.reload();
            sysVersionMapper.addVersion(container.getId());
            container.addVersion();
            if (log.isDebugEnabled()) {
                log.debug("数据容器[" + container.getId() + "]更新版本为：" + container.getVersion());
            }
        }
    }

    public void reloadContainer(String containerId) {
        reloadContainer(containerMap.get(containerId));
    }

}
