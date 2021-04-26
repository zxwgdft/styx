package com.styx.data.config;

import com.styx.common.service.mybatis.CommonSqlInjector;
import com.styx.common.spring.SpringBeanHelper;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author TontoZhou
 * @since 2019/12/10
 */
@Slf4j
@Configuration
public class DataConfiguration {


    /**
     * 负载均衡加持的RestTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


    /**
     * netty提供的线程池，这里主要处理带有sql、rpc等操作的业务，所以等待时间占比较高，
     * 可以根据占比设置一个较高的线程池数量，这里设置了4倍线程池
     * @return
     */
    @Bean
    public EventExecutorGroup getEventExecutorGroup() {
        int processorSize = Runtime.getRuntime().availableProcessors();
        return new DefaultEventExecutorGroup(processorSize * 4);
    }

    //---------------------------------------
    //
    // 以下注入为系统通用实例，与具体业务和项目无关
    //
    //---------------------------------------


    /**
     * spring bean 获取帮助类
     */
    @Bean
    public SpringBeanHelper springBeanHolder() {
        return new SpringBeanHelper();
    }

    /**
     * 扩展mybatis plus 通用方法
     */
    @Bean
    public CommonSqlInjector getCommonSqlInjector() {
        return new CommonSqlInjector();
    }

}
