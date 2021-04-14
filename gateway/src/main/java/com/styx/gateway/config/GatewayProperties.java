package com.styx.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/1/26
 */
@Getter
@Setter
@ConfigurationProperties("gateway")
public class GatewayProperties {

    private Map<String, String> staticResource = new LinkedHashMap<>();

    private String appPathPattern = "/static/app/**";

    private String appResourcePath;

}
