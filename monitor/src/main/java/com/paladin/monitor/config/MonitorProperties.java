package com.paladin.monitor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author TontoZhou
 * @since 2019/12/27
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "styx.monitor")
public class MonitorProperties {


}
