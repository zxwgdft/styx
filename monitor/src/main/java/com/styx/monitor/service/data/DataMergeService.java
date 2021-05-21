package com.styx.monitor.service.data;

import com.styx.common.config.ZKConstants;
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

    private volatile boolean connected = false;
    private volatile boolean isLeader = false;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String participantId = InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
        LeaderLatch leaderLatch = new LeaderLatch(curatorFramework, ZKConstants.PATH_DATA_MERGE, participantId);

        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                isLeader = true;
                log.info("我是leader了");
            }

            @Override
            public void notLeader() {
                isLeader = false;
                log.info("我不是leader了");
            }
        });

        leaderLatch.start();

        ScheduledFuture scheduledFuture = Executors.newSingleThreadScheduledExecutor((Runnable r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("data-merge");
            return thread;
        }).scheduleWithFixedDelay(() -> {
            log.info("leader就要干活");
        }, 1, 1, TimeUnit.MINUTES);
    }

}
