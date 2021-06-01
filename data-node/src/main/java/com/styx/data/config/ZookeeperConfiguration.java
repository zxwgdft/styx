package com.styx.data.config;

import com.styx.common.config.ZKConstants;
import com.styx.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author TontoZhou
 * @since 2021/5/18
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperConfiguration {

    @Bean
    public CuratorFramework getCuratorFramework(ZookeeperProperties properties, Environment environment) {
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .namespace(properties.getNamespace())
                        .connectString(properties.getConnectString())
                        .sessionTimeoutMs(properties.getSessionTimeout())
                        .connectionTimeoutMs(properties.getConnectionTimeout())
                        .retryPolicy(new RetryForever(properties.getRetryInterval()))
                        .build();

        curatorFramework.getConnectionStateListenable().addListener((CuratorFramework client, ConnectionState newState) -> {
            if (newState.isConnected()) {
                String applicationName = environment.getProperty("spring.application.name");
                String path = ZKConstants.PATH_DATA_SERVER + "/" + applicationName;
                try {
                    client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                    log.info("成功注册服务：" + path);
                } catch (Exception e) {
                    throw new BusinessException("注册服务到zookeeper异常", e);
                }
            }
        });

        curatorFramework.start();
        return curatorFramework;
    }

}
