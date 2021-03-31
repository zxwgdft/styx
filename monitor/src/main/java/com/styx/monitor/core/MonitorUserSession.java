package com.styx.monitor.core;

import com.styx.monitor.config.shiro.ShiroRedisSessionDAO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

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

    private String[] roleIds;
    private boolean isSystemAdmin = false;


    public MonitorUserSession() {
        super();
    }

    public MonitorUserSession(String host) {
        super(host);
    }

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
        this.roleIds = roleIds;

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
        Session obj =  SecurityUtils.getSubject().getSession();

        return (MonitorUserSession) obj;
    }

    public String[] getRoles() {
        return roleIds;
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
