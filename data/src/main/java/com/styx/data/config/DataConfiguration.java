package com.styx.data.config;

import com.paladin.framework.service.DataContainerManager;
import com.paladin.framework.service.ServiceSupportManager;
import com.paladin.framework.spring.SpringBeanHelper;
import com.paladin.framework.spring.SpringContainerManager;
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
     * spring container 管理器（用于spring加载完毕后运行的对象）
     */
    @Bean
    public SpringContainerManager springContainerManager() {
        return new SpringContainerManager();
    }

    /**
     * 数据容器管理器
     */
    @Bean
    public DataContainerManager getDataContainerManager() {
        return new DataContainerManager();
    }

    /**
     * service支持管理器
     */
    @Bean
    public ServiceSupportManager getServiceSupportContainer() {
        return new ServiceSupportManager();
    }

}
