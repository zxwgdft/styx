package com.styx.monitor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author TontoZhou
 * @since 2019/12/27
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {

    private String namespace;

    private String connectString;

    private int sessionTimeout = 60 * 1000;

    private int connectionTimeout = 15 * 1000;

    private int retryInterval = 5 * 1000;
}
