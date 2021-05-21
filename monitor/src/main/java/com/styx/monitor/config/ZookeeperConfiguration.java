package com.styx.monitor.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryForever;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/5/18
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperConfiguration {

    @Bean
    public CuratorFramework getCuratorFramework(ZookeeperProperties properties, List<ConnectionStateListener> connectionStateListeners) {
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .namespace(properties.getNamespace())
                        .connectString(properties.getConnectString())
                        .sessionTimeoutMs(properties.getSessionTimeout())
                        .connectionTimeoutMs(properties.getConnectionTimeout())
                        .retryPolicy(new RetryForever(properties.getRetryInterval()))
                        .build();

        if (connectionStateListeners != null && connectionStateListeners.size() > 0) {
            connectionStateListeners.stream().forEach((listener) ->
                    curatorFramework.getConnectionStateListenable().addListener(listener));
        }

        curatorFramework.start();
        return curatorFramework;
    }

}
