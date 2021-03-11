package com.paladin.monitor.core;

import com.paladin.monitor.model.sys.SysUser;
import com.paladin.monitor.service.sys.SysUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class MonitorUserRealm extends AuthorizingRealm {

    /**
     * shiro引用的bean需要加上lazy加载，否则会出现由于bean提前加载而无法实现代理等处理，从而导致事务，切面失效
     */
    @Lazy
    @Autowired
    private SysUserService sysUserService;

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
        SysUser sysUser = sysUserService.getUserByAccount(username);
        if (sysUser == null) {
            throw new UnknownAccountException();
        }
        return new SimpleAuthenticationInfo(username, sysUser.getPassword(), ByteSource.Util.bytes(sysUser.getSalt()),
                getName());
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
