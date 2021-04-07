package com.styx.monitor.config;

import com.styx.common.cache.DataCacheManager;
import com.styx.common.cache.RedisDataCacheManager;
import com.styx.common.service.ServiceSupportManager;
import com.styx.common.spring.SpringBeanHelper;
import com.styx.monitor.config.shiro.ShiroRedisSessionDAO;
import com.styx.monitor.core.MonitorUserRealm;
import com.styx.monitor.core.log.OperationLogInterceptor;
import com.styx.monitor.core.security.PermissionMethodInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * @author TontoZhou
 * @since 2019/12/10
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MonitorProperties.class)
public class MonitorConfiguration {


    /**
     * 负载均衡加持的RestTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


    /**
     * 数据缓存管理器
     */
    @Bean
    public DataCacheManager getDataCacheManager(@Qualifier("stringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        return new RedisDataCacheManager(redisTemplate);
    }

    /**
     * 启用登录Realm
     */
    @Bean
    public AuthorizingRealm getRealm() {
        MonitorUserRealm realm = new MonitorUserRealm();
        realm.setAuthenticationTokenClass(UsernamePasswordToken.class);
        return realm;
    }

    @Bean
    public SessionFactory getSessionFactory() {
        return new ShiroRedisSessionDAO.ControlledSessionFactory();
    }

    @Bean
    public PermissionMethodInterceptor getPermissionMethodInterceptor() {
        return new PermissionMethodInterceptor();
    }

    // 操作日志aop
    @Bean
    public OperationLogInterceptor getOperationLogInterceptor() {
        return new OperationLogInterceptor();
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

    @Bean
    public ServiceSupportManager getServiceSupportManager() {
        return new ServiceSupportManager();
    }

}
