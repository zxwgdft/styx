package com.styx.monitor.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.monitor.model.sys.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Update("UPDATE sys_user SET account = #{nowAccount}, update_time=now() WHERE user_id = #{userId}")
    int updateAccount(@Param("userId") String userId, @Param("nowAccount") String nowAccount);

    @Select("SELECT id, `account`, `password`, salt, user_id userId, user_type userType, state, last_login_time lastLoginTime FROM sys_user WHERE `account` = #{account}")
    SysUser getUserByAccount(@Param("account") String account);
}
