package com.paladin.monitor.config;

import com.paladin.monitor.config.shiro.*;
import org.apache.shiro.authc.AbstractAuthenticator;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <h2>shiro配置</h2>
 * <p>
 * 修改了部分shiro的代码，从而提高效率，减少session的重复读取
 * </p>
 *
 * @author TontoZhou
 * @since 2018年3月21日
 */
@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
public class MonitorShiroConfiguration {

    @Bean
    public ShiroRedisSessionDAO redisSessionDAO(ShiroProperties shiroProperties, RedisTemplate<String, Object> jdkRedisTemplate) {
        ShiroRedisSessionDAO sessionDao = new ShiroRedisSessionDAO(shiroProperties, jdkRedisTemplate);
        return sessionDao;
    }

    @Bean(name = "sessionManager")
    public DefaultWebSessionManager defaultWebSessionManager(ShiroProperties shiroProperties, @Autowired(required = false) ShiroRedisSessionDAO redisSessionDAO) {
        DefaultWebSessionManager sessionManager = new ShiroWebSessionManager(shiroProperties);

        // 如果设置集群共享session，需要redis来存放session
        sessionManager.setSessionDAO(redisSessionDAO);
        // 用户权限，认证等缓存设置，因为验证权限部分用其他方式实现，所以不需要缓存
        // sessionManager.setCacheManager(new RedisCacheManager());
        sessionManager.setSessionFactory(new ShiroRedisSessionDAO.ControlledSessionFactory());

        // session 监听
        // Collection<SessionListener> sessionListeners = new ArrayList<>();
        // sessionListeners.add(new CustomSessionListener());
        // sessionManager.setSessionListeners(sessionListeners);

        // 单位为毫秒（1秒=1000毫秒） 3600000毫秒为1个小时
        //sessionManager.setSessionValidationInterval(3600000);
        // 是否开启 检测，默认开启
        // sessionManager.setSessionValidationSchedulerEnabled(shiroProperties.isSessionValidationSchedulerEnabled());
        // 3600000 milliseconds = 1 hour
        // sessionManager.setGlobalSessionTimeout(shiroProperties.getSessionTime() * 60 * 1000);
        // 是否删除无效的，默认也是开启
        sessionManager.setDeleteInvalidSessions(false);
        // 是否在url上显示检索得到的sessionid
        sessionManager.setSessionIdUrlRewritingEnabled(false);

        return sessionManager;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManage(DefaultWebSessionManager defaultWebSessionManager, List<Realm> realms,
                                                                 List<AuthenticationListener> authenticationListeners) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setAuthenticator(new ShiroMultiRealmAuthenticator());
        securityManager.setRealms(realms);

        // 这是shiro提供的验证成功失败接口，如果在filter中处理登录成功失败不一定能覆盖所有情况
        Authenticator authenticator = securityManager.getAuthenticator();
        if (authenticator instanceof AbstractAuthenticator) {
            ((AbstractAuthenticator) authenticator).setAuthenticationListeners(authenticationListeners);
        }

        // 注入缓存管理器;
        // securityManager.setCacheManager(redisCacheManager());
        securityManager.setSessionManager(defaultWebSessionManager);
        return securityManager;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shirFilter(DefaultWebSecurityManager securityManager, ShiroProperties shiroProperties) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 增加自定义过滤
        Map<String, Filter> filters = new HashMap<>();

        ShiroAuthenticationFilter authenticationFilter = new ShiroAuthenticationFilter();
        authenticationFilter.setLoginUrl(shiroProperties.getLoginUrl());

        filters.put("authc", authenticationFilter);

        shiroFilterFactoryBean.setFilters(filters);
        // 拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

        // anon（匿名） org.apache.shiro.web.filter.authc.AnonymousFilter
        // authc（身份验证） org.apache.shiro.web.filter.authc.FormAuthenticationFilter
        // authcBasic（http基本验证）org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
        // logout（退出） org.apache.shiro.web.filter.authc.LogoutFilter
        // noSessionCreation org.apache.shiro.web.filter.session.NoSessionCreationFilter
        // perms(许可验证) org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter
        // port（端口验证） org.apache.shiro.web.filter.authz.PortFilter
        // rest (rest方面) org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter
        filterChainDefinitionMap.put("/monitor/login/**", "anon");
        filterChainDefinitionMap.put("/monitor/verifyCode", "anon");
        filterChainDefinitionMap.put("/monitor/auth/redirect", "anon");
        filterChainDefinitionMap.put("/monitor/auth/token", "anon");
        filterChainDefinitionMap.put("/monitor/**", "authc");
        filterChainDefinitionMap.put("/**", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean(name = "authenticationStrategy")
    public AuthenticationStrategy authenticationStrategy() {
        return new FirstSuccessfulStrategy();
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


}
