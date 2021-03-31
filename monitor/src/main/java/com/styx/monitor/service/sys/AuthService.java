package com.styx.monitor.service.sys;

import com.styx.monitor.core.MonitorUserSession;
import com.styx.monitor.service.sys.dto.LoginSuccess;
import com.styx.monitor.service.sys.dto.LoginUser;
import com.styx.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/1/19
 */
@Slf4j
@Service
public class AuthService {

    private final static Map<Class, String> exceptionMessageMap;

    static {
        exceptionMessageMap = new HashMap<>();
        exceptionMessageMap.put(IncorrectCredentialsException.class, "账号密码不正确");
        exceptionMessageMap.put(ExpiredCredentialsException.class, "账号密码过期");
        exceptionMessageMap.put(CredentialsException.class, "账号密码异常");
        exceptionMessageMap.put(ConcurrentAccessException.class, "无法同时多个用户登录");
        exceptionMessageMap.put(UnknownAccountException.class, "账号不存在");
        exceptionMessageMap.put(ExcessiveAttemptsException.class, "账号验证次数超过限制");
        exceptionMessageMap.put(LockedAccountException.class, "账号被锁定");
        exceptionMessageMap.put(DisabledAccountException.class, "账号被禁用");
        exceptionMessageMap.put(AccountException.class, "账号异常");
        exceptionMessageMap.put(UnsupportedTokenException.class, "不支持当前TOKEN");
    }

    private void parseAuthException(RuntimeException e) {
        String message = exceptionMessageMap.get(e.getClass());
        if (message != null) {
            throw new BusinessException(message);
        }
        throw e;
    }

    public LoginSuccess auth() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return getLoginSuccess((String) subject.getSession().getId());
        }
        return null;
    }

    public LoginSuccess auth(LoginUser loginUser) {
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(new UsernamePasswordToken(loginUser.getUsername(), loginUser.getPassword(), loginUser.isRememberMe()));
        } catch (RuntimeException e) {
            parseAuthException(e);
        }
        return getLoginSuccess((String) subject.getSession().getId());
    }

    private LoginSuccess getLoginSuccess(String token) {
        MonitorUserSession userSession = MonitorUserSession.getCurrentUserSession();

        LoginSuccess response = new LoginSuccess();
        response.setUsername(userSession.getUserName());
        response.setUserType(userSession.getUserType());
        response.setSystemAdmin(userSession.isSystemAdmin());
        response.setToken(token);

        return response;
    }


}
