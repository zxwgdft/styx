package com.styx.monitor.core;

import com.styx.common.utils.StringUtil;
import com.styx.common.utils.secure.SecureUtil;
import com.styx.monitor.mapper.org.OrgUserMapper;
import com.styx.monitor.mapper.sys.SysUserMapper;
import com.styx.monitor.model.org.OrgUser;
import com.styx.monitor.model.sys.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class MonitorUserRealm extends AuthorizingRealm {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private OrgUserMapper orgUserMapper;


    /**
     * 认证信息.(身份验证) : Authentication 是用来验证用户身份
     *
     * @param authToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authToken;
        String username = token.getUsername();
        String password = new String(token.getPassword());

        SysUser sysUser = sysUserMapper.getUserByAccount(username);
        if (sysUser == null) {
            throw new UnknownAccountException();
        }

        int state = sysUser.getState();

        if (state == SysUser.STATE_ENABLED) {
            // 判断密码是否正确
            String hashPassword = SecureUtil.hashByMD5(password, sysUser.getSalt());
            if (!hashPassword.equals(sysUser.getPassword())) {
                throw new IncorrectCredentialsException();
            }
            MonitorUserSession userSession = createUserSession(sysUser);
            return new SimpleAuthenticationInfo(userSession, password, getName());
        } else {
            // 账号状态异常
            switch (state) {
                case SysUser.STATE_DISABLED:
                    throw new DisabledAccountException();
                default:
                    throw new AccountException();
            }
        }
    }


    private MonitorUserSession createUserSession(SysUser sysUser) {

        int type = sysUser.getUserType();
        int state = sysUser.getState();

        MonitorUserSession userSession = new MonitorUserSession();

        if (type == SysUser.USER_TYPE_ADMIN) {
            userSession.setSystemAdmin(true);
            userSession.setUserId("admin");
            userSession.setUserName("系统管理员");
            userSession.setUserType(9999);
        } else if (type == SysUser.USER_TYPE_PERSONNEL) {
            String userId = sysUser.getUserId();
            OrgUser user = orgUserMapper.selectById(userId);
            if (user != null && !user.getDeleted()) {
                String[] roleId = user.getRole().split(",");
                int userType = user.getType();

                userSession.setUserId(userId);
                userSession.setUserName(user.getName());
                userSession.setUserType(userType);
                userSession.setRoleIds(roleId);

                if (userType == OrgUser.USER_TYPE_DISTRICT) {
                    userSession.setDistricts(convert(user.getDistrictCode()));
                } else if (userType == OrgUser.USER_TYPE_STATION) {
                    userSession.setStations(convert(user.getStationId()));
                }
            }
        }

        return userSession;
    }

    private Integer[] convert(String idStr) {
        Integer[] ids = null;
        if (StringUtil.isNotEmpty(idStr)) {
            String[] ss = idStr.split(",");
            ids = new Integer[ss.length];
            for (int i = 0; i < ss.length; i++) {
                ids[i] = Integer.valueOf(ss[i]);
            }
        }
        return ids;
    }

    /**
     * 此方法调用 hasRole,hasPermission的时候才会进行回调.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) throws AuthenticationException {
        // 废弃shiro缓存验证信息策略
        return null;
    }

    protected AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            return null;
        }
        return (AuthorizationInfo) principals.getPrimaryPrincipal();
    }

}
