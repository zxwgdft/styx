package com.paladin.monitor.core;


import com.paladin.monitor.model.org.OrgUser;
import com.paladin.monitor.model.sys.SysLoggerLogin;
import com.paladin.monitor.model.sys.SysUser;
import com.paladin.monitor.service.org.OrgUserService;
import com.paladin.monitor.service.sys.SysLoggerLoginService;
import com.paladin.monitor.service.sys.SysUserService;
import com.styx.common.utils.StringUtil;
import com.styx.common.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.WebSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * UserSession创建器
 *
 * @author TontoZhou
 * @since 2020/3/17
 */
@Slf4j
public class MonitorUserSessionFactory implements SessionFactory, AuthenticationListener {

    /**
     * shiro引用的bean需要加上lazy加载，否则会出现由于bean提前加载而无法实现代理等处理，从而导致事务，切面失效
     */

    @Lazy
    @Autowired
    private SysLoggerLoginService sysLoggerLoginService;

    @Lazy
    @Autowired
    private SysUserService sysUserService;

    @Lazy
    @Autowired
    private OrgUserService orgUserService;


    @Override
    public Session createSession(SessionContext initData) {
        return new MonitorUserSession();
    }

    @Override
    public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
        MonitorUserSession userSession = MonitorUserSession.getCurrentUserSession();

        String ip = null;
        if (token instanceof HostAuthenticationToken) {
            ip = ((HostAuthenticationToken) token).getHost();
        }

        if (ip == null) {
            // 获取request ip
            WebSubject webSubject = (WebSubject) SecurityUtils.getSubject();
            HttpServletRequest request = (HttpServletRequest) webSubject.getServletRequest();
            ip = WebUtil.getIpAddress(request);
        }

        if (ip == null) {
            ip = "unknown";
        }

        SysUser sysUser = sysUserService.getUserByAccount((String) token.getPrincipal());
        if (sysUser == null) {
            throw new UnknownAccountException();
        }

        initUserSessionData(userSession, sysUser);

        String account = userSession.getAccount();

        SysLoggerLogin model = new SysLoggerLogin();
        model.setIp(ip);
        model.setAccount(account);
        // 待扩展
        model.setUserType(1);
        // 待扩展
        model.setLoginType(1);
        model.setUserId(userSession.getUserId());
        model.setCreateTime(new Date());

        sysLoggerLoginService.save(model);
        sysUserService.updateLastTime(account);

        log.info("===>用户[" + account + "]登录系统<===");
    }

    @Override
    public void onFailure(AuthenticationToken token, AuthenticationException ae) {

    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        log.info("===>用户[" + principals.getPrimaryPrincipal() + "]登出系统<===");
    }


    private void initUserSessionData(MonitorUserSession userSession, SysUser sysUser) {
        if (sysUser == null) {
            throw new UnknownAccountException("账号不存在");
        }

        int type = sysUser.getUserType();
        int state = sysUser.getState();

        if (state == SysUser.STATE_ENABLED) {
            if (type == SysUser.USER_TYPE_ADMIN) {
                userSession.initSystemAdminData("admin", "系统管理员", sysUser.getAccount());
                return;
            }
            if (type == SysUser.USER_TYPE_PERSONNEL) {
                String userId = sysUser.getUserId();
                OrgUser user = orgUserService.get(userId);
                if (user != null) {
                    String[] roleId = user.getRole().split(",");
                    int userType = user.getType();
                    if (userType == OrgUser.USER_TYPE_DISTRICT) {
                        int[] dc = StringUtil.stringToIntArray(user.getDistrictCode());
                        userSession.initData(userId, user.getName(), sysUser.getAccount(), roleId, dc, null, userType);
                        return;
                    } else if (userType == OrgUser.USER_TYPE_STATION) {
                        int[] stations = StringUtil.stringToIntArray(user.getStationId());
                        userSession.initData(userId, user.getName(), sysUser.getAccount(), roleId, null, stations, userType);
                        return;
                    } else {
                        userSession.initData(userId, user.getName(), sysUser.getAccount(), roleId, null, null, userType);
                        return;
                    }
                }
            }
        }

        if (state == SysUser.STATE_DISABLED) {
            throw new DisabledAccountException();
        }

        throw new AuthenticationException("账号异常");
    }


}
