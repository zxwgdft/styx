package com.paladin.monitor.core;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign方式统一request模板拦截器
 *
 * @author TontoZhou
 * @since 2020/9/1
 */
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        MonitorUserSession userSession = MonitorUserSession.getCurrentUserSession();
//        template.header(WebSecurityManager.HEADER_USER_ID, userSession.getUserId());
//        template.header(WebSecurityManager.HEADER_USER_TYPE, userSession.getUserType());
    }
}
