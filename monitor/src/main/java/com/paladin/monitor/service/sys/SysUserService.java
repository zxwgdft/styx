package com.paladin.monitor.service.sys;

import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.Condition;
import com.paladin.framework.service.QueryType;
import com.paladin.framework.service.ServiceSupport;
import com.paladin.framework.service.UserSession;
import com.paladin.framework.utils.UUIDUtil;
import com.paladin.framework.utils.secure.SecureUtil;
import com.paladin.monitor.mapper.sys.SysUserMapper;
import com.paladin.monitor.model.sys.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SysUserService extends ServiceSupport<SysUser> {

    @Value("${monitor.default-password:1}")
    private String defaultPassword;

    @Value("${monitor.default-password-random:false}")
    private boolean randomPassword;

    @Autowired
    private SysUserMapper sysUserMapper;


    private Pattern accountPattern = Pattern.compile("^\\w{6,30}$");
    private Pattern passwordPattern = Pattern.compile("^\\w{6,20}$");


    private String getDefaultPassword() {
        if (randomPassword) {
            return UUIDUtil.createUUID().substring(0, 10);
        }
        return defaultPassword;
    }

    /**
     * 创建一个账号
     */
    public String createUserAccount(String account, String userId, Integer type) {
        if (account == null || !validateAccount(account)) {
            throw new BusinessException("账号不符合规则或者已经存在该账号");
        }

        String salt = SecureUtil.createSalt();

        String originPassword = getDefaultPassword();
        String password = SecureUtil.hashByMD5(originPassword, salt);

        SysUser user = new SysUser();
        user.setAccount(account);
        user.setPassword(password);
        user.setSalt(salt);
        user.setUserId(userId);
        user.setState(SysUser.STATE_ENABLED);
        user.setUserType(type);
        user.setCreateTime(new Date());
        save(user);

        return originPassword;
    }


    /**
     * 验证账号
     *
     * @return true 可用/false 不可用
     */
    public boolean validateAccount(String account) {
        if (!accountPattern.matcher(account).matches()) {
            return false;
        }
        return searchCount(new Condition(SysUser.FIELD_ACCOUNT, QueryType.EQUAL, account)) == 0;
    }

    /**
     * 通过账号查找用户
     */
    public SysUser getUserByAccount(String account) {
        List<SysUser> users = searchAll(new Condition(SysUser.FIELD_ACCOUNT, QueryType.EQUAL, account));
        return (users != null && users.size() > 0) ? users.get(0) : null;
    }

    /**
     * 更新登录人密码
     */
    public void updateSelfPassword(String newPassword, String oldPassword) {
        if (newPassword == null || !passwordPattern.matcher(newPassword).matches()) {
            throw new BusinessException("密码不符合规则");
        }

        UserSession session = UserSession.getCurrentUserSession();
        String account = session.getAccount();
        SysUser user = getUserByAccount(account);
        if (user == null) {
            throw new BusinessException("账号异常");
        }

        oldPassword = SecureUtil.hashByMD5(oldPassword, user.getSalt());
        if (!oldPassword.equals(user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        String salt = SecureUtil.createSalt();
        newPassword = SecureUtil.hashByMD5(newPassword, salt);

        SysUser updateUser = new SysUser();
        updateUser.setId(user.getId());
        updateUser.setSalt(salt);
        updateUser.setPassword(newPassword);
        updateUser.setCreateTime(new Date());

        updateSelective(updateUser);

        // 是否要求重新登录
        // SecurityUtils.getSubject().logout();
    }


    /**
     * 更新个人用户账号
     */
    public void updateUserAccount(String userId, String newAccount) {
        sysUserMapper.updateAccount(userId, newAccount);
    }

    /**
     * 删除个人用户账号
     */
    public void removeUserAccount(String userId) {
        remove(new Condition(SysUser.FIELD_USER_ID, QueryType.EQUAL, userId));
    }

    /**
     * 重置密码
     */
    public String reset(String userId) {
        SysUser sysUser = sysUserMapper.selectOneByExample(new Example.Builder(SysUser.class).where(WeekendSqls.<SysUser>custom().andEqualTo(SysUser::getUserId, userId)).build());
        if (sysUser != null) {
            String originPassword = getDefaultPassword();
            String salt = SecureUtil.createSalt();
            String password = SecureUtil.hashByMD5(originPassword, salt);
            sysUser.setPassword(password);
            sysUser.setSalt(salt);
            updateSelective(sysUser);
            return originPassword;
        }
        throw new BusinessException("用户不存在");
    }

    public void updateLastTime(String account) {
        // 不更新update_time
        SysUser sysUser = getUserByAccount(account);
        SysUser user = new SysUser();
        user.setId(sysUser.getId());
        user.setLastLoginTime(new Date());
        updateSelective(user);
    }

    public SysUser getUserByWX(String openId) {
        List<SysUser> users = searchAll(new Condition(SysUser.FIELD_WX_ID, QueryType.EQUAL, openId));
        return (users != null && users.size() > 0) ? users.get(0) : null;
    }

    public void setWX_IDToUser(String userId, String openId) {
        sysUserMapper.updateWX_ID(userId, openId);
    }

    public void setWX_IDToNull(String userId, String openId) {
        sysUserMapper.updateWX_ID2Null(userId, openId);
    }


}
