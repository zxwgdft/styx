package com.paladin.monitor.service.sys;

import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.utils.UUIDUtil;
import com.paladin.framework.utils.WebUtil;
import com.paladin.monitor.config.BusinessProperties;
import com.paladin.monitor.core.MonitorUserSession;
import com.paladin.monitor.core.WeiXinAuthenticationToken;
import com.paladin.monitor.model.sys.SysUser;
import com.paladin.monitor.service.sys.dto.LoginSuccess;
import com.paladin.monitor.service.sys.dto.LoginUser;
import com.paladin.monitor.service.sys.dto.RedirectLoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private VerifyCodeService verifyCodeService;
    @Autowired
    private SupportService supportService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate<String, Object> jdkRedisTemplate;

    @Value("${monitor.config.auth-redirect-url}")
    private String redirectUrl;
    @Resource
    private BusinessProperties businessProperties;

    private static final String AUTH_TOKEN_PREFIX = "_TOKEN_";

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
        if (!verifyCodeService.validVerifyCode(loginUser.getCode())) {
            throw new BusinessException("验证码不正确或已失效");
        }
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
        response.setPermissionCodes(userSession.getPermissionCodes());
        response.setMenus(userSession.getMenuResources());

        return response;
    }

    public String authAndGetRedirectUrl(RedirectLoginUser loginUser, HttpServletRequest request) {
        //TODO 没有验证码过滤，这里可建立一个简单的请求地址白名单，防止暴力账号密码破解
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(new UsernamePasswordToken(loginUser.getUsername(), loginUser.getPassword(), false));
        } catch (RuntimeException e) {
            parseAuthException(e);
        }

        LoginSuccess response = getLoginSuccess((String) subject.getSession().getId());
        String token = UUIDUtil.createUUID();
        jdkRedisTemplate.opsForValue().set(getRedisKey(token), response, 2, TimeUnit.MINUTES);
        String url = redirectUrl.startsWith("http") ? redirectUrl
                : WebUtil.getServletPath(request) + redirectUrl;

        StringBuilder sb = new StringBuilder(url);

        if (url.indexOf("?") <= 0) {
            sb.append("?token=").append(token);
        } else {
            sb.append("&token=").append(token);
        }

        String forward = loginUser.getForward();
        if (forward != null && forward.length() > 0) {
            sb.append("&forward=").append(forward);
        }

        String layout = loginUser.getLayout();
        if (layout != null && layout.length() > 0) {
            sb.append("&layout=").append(layout);
        }

        return sb.toString();
    }

    /**
     * 通过跳转token获取验证结果
     */
    public LoginSuccess getAuthResultByToken(String token) {

        // 2021-05-01前可直接用固定token登录，临时提供给苏州接口
        if (System.currentTimeMillis() < 1619849210000L) {
            List<BusinessProperties.NoSecretToken> noSecretTokens = businessProperties.getNoSecretTokens();
            if (noSecretTokens != null) {
                for (BusinessProperties.NoSecretToken noSecretToken : noSecretTokens) {
                    if (noSecretToken.getToken().equals(token)) {
                        try {
                            Subject subject = SecurityUtils.getSubject();
                            subject.login(new UsernamePasswordToken(noSecretToken.getAccount(), noSecretToken.getPassword(), false));
                            return getLoginSuccess((String) subject.getSession().getId());
                        } catch (RuntimeException e) {
                            throw new BusinessException("用户认证未通过或已过期");
                        }
                    }
                }
            }
        }

        LoginSuccess result = (LoginSuccess) jdkRedisTemplate.opsForValue().get(getRedisKey(token));
        if (result != null) {
            return result;
        }
        throw new BusinessException("用户认证未通过或已过期");
    }

    private String getRedisKey(String token) {
        return AUTH_TOKEN_PREFIX + token;
    }

    /**
     * 微信登录
     */
    public LoginSuccess authWX(String jsCode) {
        String openId = getOpenIdOfWX(jsCode);
        if (openId != null && openId.length() > 0) {
            try {
                Subject subject = SecurityUtils.getSubject();
                subject.login(new WeiXinAuthenticationToken(openId));
                return getLoginSuccess((String) subject.getSession().getId());
            } catch (Exception e) {
                // do nothing
            }
        }
        return null;
    }

    /**
     * 账号绑定微信
     */
    public void bindWeiXin(String jsCode) {
        String openId = getOpenIdOfWX(jsCode);
        if (openId != null && openId.length() > 0) {
            MonitorUserSession userSession = MonitorUserSession.getCurrentUserSession();
            if (userSession.isSystemAdmin()) {
                throw new BusinessException("系统管理员无法绑定微信");
            }
            SysUser sysUser = sysUserService.getUserByWX(openId);
            if (sysUser != null) {
                throw new BusinessException("当前微信已经被账号[" + sysUser.getAccount() + "]绑定，前先解绑");
            }
            sysUserService.setWX_IDToUser(userSession.getUserId(), openId);
        }
    }

    /**
     * 账号解绑微信
     */
    public void unbindWeiXin(String jsCode) {
        String openId = getOpenIdOfWX(jsCode);
        if (openId != null && openId.length() > 0) {
            MonitorUserSession userSession = MonitorUserSession.getCurrentUserSession();
            if (userSession.isSystemAdmin()) {
                throw new BusinessException("系统管理员无法绑定微信");
            }
            sysUserService.setWX_IDToNull(userSession.getUserId(), openId);
        }
    }

    /**
     * 获取微信openid
     */
    private String getOpenIdOfWX(String jsCode) {
        try {
            return supportService.getWXOpenId(jsCode);
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }


}
