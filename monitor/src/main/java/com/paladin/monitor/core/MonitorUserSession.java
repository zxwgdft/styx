package com.paladin.monitor.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paladin.monitor.config.shiro.ShiroRedisSessionDAO;
import com.paladin.monitor.core.security.Menu;
import com.paladin.monitor.core.security.Role;
import com.paladin.monitor.core.security.RoleContainer;
import com.styx.common.exception.BusinessException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;

import java.util.*;

/**
 * 通用用户会话信息
 *
 * @author TontoZhou
 * @since 2019年7月24日
 */
public class MonitorUserSession extends ShiroRedisSessionDAO.ControlledSession {

    private String userId;
    private String userName;
    private String account;
    private int userType;
    private int[] stations;
    private int[] districts;

    protected List<String> roleIds;
    protected boolean isSystemAdmin = false;

    /**
     * 非超级管理员初始化方法
     */
    public void initData(String userId, String userName, String account, String[] roleIds, int[] districts, int[] stations, int userType) {
        this.userId = userId;
        this.userName = userName;
        this.account = account;
        this.stations = stations;
        this.districts = districts;
        this.userType = userType;

        setRoleId(roleIds);

        this.forceUpdate();
    }

    /**
     * 超级管理员初始化方法
     */
    public void initSystemAdminData(String userId, String userName, String account) {
        this.userId = userId;
        this.userName = userName;
        this.account = account;
        this.isSystemAdmin = true;
        this.userType = 9999;

        this.forceUpdate();
    }

    /**
     * 获取当前用户会话
     */
    public static MonitorUserSession getCurrentUserSession() {
        return (MonitorUserSession) SecurityUtils.getSubject().getSession();
    }

    /**
     * 设置角色ID
     */
    private void setRoleId(String... roleIds) {
        List<String> roleIdList = new ArrayList<>(roleIds.length);
        for (int i = 0; i < roleIds.length; i++) {
            String roleId = roleIds[i];
            if (roleId != null) {
                Role role = RoleContainer.getRole(roleId);
                if (role != null) {
                    roleIdList.add(roleId);
                }
            }
        }

        this.roleIds = roleIdList;
    }

    /**
     * 菜单资源
     *
     * @return
     */
    public List<Menu> getMenuResources() {
        if (isSystemAdmin) {
            return RoleContainer.getSystemAdminRole().getRootMenus();
        }

        if (roleIds.size() == 1) {
            Role role = RoleContainer.getRole(roleIds.get(0));
            if (role == null) {
                throw new BusinessException("登录用户角色异常");
            }
            return role.getRootMenus();
        }

        ArrayList<Role> roles = new ArrayList<>(roleIds.size());
        for (String rid : roleIds) {
            Role role = RoleContainer.getRole(rid);
            if (role != null) {
                roles.add(role);
            }
        }

        if (roles.size() == 0) {
            throw new BusinessException("登录用户角色异常");
        }

        return RoleContainer.getMultiRoleMenu(roles);
    }

    @JsonIgnore
    public Collection<String> getRoles() {
        return roleIds;
    }

    @JsonIgnore
    public Collection<String> getStringPermissions() {
        // 返回权限字符串数组，这里返回null，如果
        return null;
    }

    @JsonIgnore
    public Collection<Permission> getObjectPermissions() {
        if (isSystemAdmin) {
            return (List) RoleContainer.getSystemAdminRole().getPermissions();
        }

        if (roleIds.size() == 1) {
            Role role = RoleContainer.getRole(roleIds.get(0));
            if (role == null) {
                return null;
            } else {
                return (List) role.getPermissions();
            }
        }

        ArrayList<Role> roles = new ArrayList<>(roleIds.size());
        for (String rid : roleIds) {
            Role role = RoleContainer.getRole(rid);
            if (role != null) {
                roles.add(role);
            }
        }

        if (roles.size() == 0) {
            return null;
        }

        return (List) RoleContainer.getMultiRolePermission(roles);
    }

    /**
     * 获取所有权限的CODE集合，可用于简单方式判断权限
     */
    @JsonIgnore
    public Set<String> getPermissionCodes() {
        if (isSystemAdmin) {
            return RoleContainer.getSystemAdminRole().getPermissionCodes();
        }

        if (roleIds.size() == 1) {
            Role role = RoleContainer.getRole(roleIds.get(0));
            if (role == null) {
                return null;
            } else {
                return role.getPermissionCodes();
            }
        }


        Set<String> codeSet = new HashSet<>();
        for (String rid : roleIds) {
            Role role = RoleContainer.getRole(rid);
            if (role != null) {
                codeSet.addAll(role.getPermissionCodes());
            }
        }

        return codeSet;
    }

    public boolean isSystemAdmin() {
        return isSystemAdmin;
    }


    public int getUserType() {
        return userType;
    }

    public int[] getStations() {
        return stations;
    }

    public int[] getDistricts() {
        return districts;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccount() {
        return account;
    }

    public String toString() {
        return "用户名：" + getUserName() + "/账号：" + getAccount();
    }

}
