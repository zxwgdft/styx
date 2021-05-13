package com.styx.support.core;

import com.styx.common.config.GlobalConstants;
import com.styx.common.config.RedisConstants;
import com.styx.common.exception.BusinessException;
import com.styx.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author TontoZhou
 * @since 2020/8/18
 */
@Component
public class SupportSecurityManager implements HandlerInterceptor {

    @Value("${support.security.token-filed:Authorization}")
    private String tokenFiled;

    @Autowired
    private RedisTemplate<String, Object> jdkRedisTemplate;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(tokenFiled);
        if (StringUtil.isNotEmpty(token)) {
            // 暂时只做是否存在token校验
            if (jdkRedisTemplate.hasKey(RedisConstants.WEB_SESSION_PREFIX + token)) {
                return true;
            }
        }
        throw new BusinessException(HttpStatus.UNAUTHORIZED, "未认证，请先登录");
    }


}
