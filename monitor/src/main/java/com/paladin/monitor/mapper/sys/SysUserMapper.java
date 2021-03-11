package com.paladin.monitor.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.model.sys.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Update("UPDATE sys_user SET account = #{nowAccount}, update_time=now() WHERE user_id = #{userId}")
    int updateAccount(@Param("userId") String userId, @Param("nowAccount") String nowAccount);

    @Update("UPDATE sys_user SET wx_id = #{openId}, update_time=now() WHERE user_id = #{userId}")
    int updateWX_ID(@Param("userId") String userId, @Param("openId") String openId);

    @Update("UPDATE sys_user SET wx_id = null, update_time=now() WHERE user_id = #{userId} AND wx_id = #{openId}")
    int updateWX_ID2Null(@Param("userId") String userId, @Param("openId") String openId);
}
