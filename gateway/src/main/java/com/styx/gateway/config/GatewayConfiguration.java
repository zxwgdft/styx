package com.styx.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/1/26
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({GatewayProperties.class})
public class GatewayConfiguration {

    @Bean
    public RouterFunction<ServerResponse> staticResourceLocator(ResourceLoader resourceLoader, GatewayProperties gatewayProperties) {
        Map<String, String> staticResources = gatewayProperties.getStaticResource();
        RouterFunctions.Builder builder = RouterFunctions.route();

        String appPathPattern = gatewayProperties.getAppPathPattern();
        String appResourcePath = gatewayProperties.getAppResourcePath();
        log.info("添加APP静态资源路由：[{}] -> [{}]", appPathPattern, appResourcePath);
        builder.add(RouterFunctions.resources(appPathPattern, resourceLoader.getResource(appResourcePath)));

        if (!staticResources.isEmpty()) {
            staticResources.forEach((key, value) -> {
                log.info("添加静态资源配置: [{}] -> [{}]", key, value);
                builder.add(RouterFunctions.resources(key, resourceLoader.getResource(value)));
            });
        }

        builder.add(RouterFunctions.resources("/favicon.ico", resourceLoader.getResource("classpath:/static/favicon.ico")));

        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "gateway", value = "cors-enabled", havingValue = "true", matchIfMissing = false)
    public CorsWebFilter corsFilter(GlobalCorsProperties globalCorsProperties) {
        Map<String, CorsConfiguration> corsConfigMap = globalCorsProperties.getCorsConfigurations();
        if (corsConfigMap.size() == 0) {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            corsConfigMap.put("/**", config);
        }
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        for (Map.Entry<String, CorsConfiguration> entry : corsConfigMap.entrySet()) {
            source.registerCorsConfiguration(entry.getKey(), entry.getValue());
        }
        return new CorsWebFilter(source);
    }


}
