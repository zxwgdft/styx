package com.styx.monitor.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.styx.common.exception.BusinessException;
import com.styx.common.utils.UUIDUtil;
import com.styx.common.utils.secure.SecureUtil;
import com.styx.monitor.core.MonitorServiceSupport;
import com.styx.monitor.mapper.sys.SysUserMapper;
import com.styx.monitor.model.sys.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Pattern;

@Service
public class SysUserService extends MonitorServiceSupport<SysUser, SysUserMapper> {

    @Value("${styx.default-password:1}")
    private String defaultPassword;

    @Value("${styx.default-password-random:false}")
    private boolean randomPassword;

    private Pattern accountPattern = Pattern.compile("^\\w{6,30}$");

    // 获取默认密码（随机或固定）
    private String getDefaultPassword() {
        if (randomPassword) {
            return UUIDUtil.createUUID().substring(0, 10);
        }
        return defaultPassword;
    }

    /**
     * 创建一个账号，并返回密码
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
     */
    public boolean validateAccount(String account) {
        if (!accountPattern.matcher(account).matches()) {
            return false;
        }
        return searchCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getAccount, account)
        ) == 0;
    }


    /**
     * 更新个人用户账号
     */
    public void updateUserAccount(String userId, String newAccount) {
        getSqlMapper().updateAccount(userId, newAccount);
    }


}
