package com.styx.monitor.core;


import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;

/**
 * UserSession创建器
 *
 * @author TontoZhou
 * @since 2020/3/17
 */
@Slf4j
public class MonitorUserSessionFactory implements SessionFactory {

    @Override
    public Session createSession(SessionContext initData) {
        if (initData != null) {
            String host = initData.getHost();
            if (host != null) {
                if (log.isDebugEnabled()) {
                    log.debug("创建ControlledSession[HOST:" + host + "]");
                }
                return create(new MonitorUserSession(host));
            }
        }

        if (log.isDebugEnabled()) {
            log.info("创建ControlledSession[HOST:无]");
        }

        return create(new MonitorUserSession());
    }

    private MonitorUserSession create(MonitorUserSession userSession) {

        return userSession;

//        int type = sysUser.getUserType();
//        int state = sysUser.getState();
//
//        if (state == SysUser.STATE_ENABLED) {
//            if (type == SysUser.USER_TYPE_ADMIN) {
//                userSession.initSystemAdminData("admin", "系统管理员", sysUser.getAccount());
//                return;
//            }
//            if (type == SysUser.USER_TYPE_PERSONNEL) {
//                String userId = sysUser.getUserId();
//                OrgUser user = orgUserService.get(userId);
//                if (user != null) {
//                    String[] roleId = user.getRole().split(",");
//                    int userType = user.getType();
//                    if (userType == OrgUser.USER_TYPE_DISTRICT) {
//                        int[] dc = StringUtil.stringToIntArray(user.getDistrictCode());
//                        userSession.initData(userId, user.getName(), sysUser.getAccount(), roleId, dc, null, userType);
//                        return;
//                    } else if (userType == OrgUser.USER_TYPE_STATION) {
//                        int[] stations = StringUtil.stringToIntArray(user.getStationId());
//                        userSession.initData(userId, user.getName(), sysUser.getAccount(), roleId, null, stations, userType);
//                        return;
//                    } else {
//                        userSession.initData(userId, user.getName(), sysUser.getAccount(), roleId, null, null, userType);
//                        return;
//                    }
//                }
//            }
//        }
//
//        if (state == SysUser.STATE_DISABLED) {
//            throw new DisabledAccountException();
//        }
//
//        throw new AuthenticationException("账号异常");
    }


}
