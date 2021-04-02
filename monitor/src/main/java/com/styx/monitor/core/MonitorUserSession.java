package com.styx.monitor.core;

import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.SecurityUtils;

import java.io.Serializable;

/**
 * 通用用户会话信息
 *
 * @author TontoZhou
 * @since 2019年7月24日
 */
@Getter
@Setter
public class MonitorUserSession implements Serializable {

    private String userId;
    private String userName;
    private int userType;
    private int[] stations;
    private int[] districts;
    private String[] roleIds;

    private boolean isSystemAdmin = false;


    /**
     * 获取当前用户会话
     */
    public static MonitorUserSession getCurrentUserSession() {
        return (MonitorUserSession) SecurityUtils.getSubject().getPrincipal();
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("用户ID：").append(userId)
                .append("用户姓名：").append(userName)
                .append("用户类型：").append(isSystemAdmin ? "系统管理员" : userType);
        return sb.toString();
    }

}
