package com.styx.monitor.service.data;

import com.styx.common.config.GlobalConstants;
import com.styx.common.config.ZKConstants;
import com.styx.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author TontoZhou
 * @since 2021/5/21
 */
@Slf4j
@Service
public class DataMergeService implements ApplicationRunner {

    @Resource
    private Environment environment;

    @Autowired
    private CuratorFramework curatorFramework;


    public void mergeData() {
        try {
            List<String> zkNodes = curatorFramework.getChildren().forPath(ZKConstants.PATH_DATA_SERVER);
            if (zkNodes != null && zkNodes.size() > 0) {
                Set<String> zkNodeSet = new HashSet<>();
                zkNodes.stream().forEach((item) -> {
                            zkNodeSet.add(item.substring(GlobalConstants.DATA_SERVICE_PREFIX.length()));
                        }
                );
            }
        } catch (Exception e) {
            throw new BusinessException("获取数据节点状态异常", e);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String participantId = InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
        LeaderLatch leaderLatch = new LeaderLatch(curatorFramework, ZKConstants.PATH_DATA_MERGE, participantId);

        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                openScheduledTask();
            }

            @Override
            public void notLeader() {
                closeScheduledTask();
            }
        });

        leaderLatch.start();
    }


    private Object lock = new Object();
    private ScheduledFuture scheduledFuture;
    private int mergeInterval = 60;

    private void closeScheduledTask() {
        synchronized (lock) {
            log.info("关闭数据合并任务");
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
                scheduledFuture = null;
            }
        }
    }

    private void openScheduledTask() {
        synchronized (lock) {
            log.info("开启数据合并任务");
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }

            scheduledFuture = Executors.newSingleThreadScheduledExecutor((Runnable r) -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("data-merge");
                return thread;
            }).scheduleWithFixedDelay(() -> {
                mergeData();
            }, 0, mergeInterval, TimeUnit.SECONDS);
        }
    }

}
