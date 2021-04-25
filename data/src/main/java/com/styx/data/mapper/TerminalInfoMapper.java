package com.styx.data.mapper;

import com.styx.common.service.mybatis.CommonMapper;
import com.styx.data.model.TerminalInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
public interface TerminalInfoMapper extends CommonMapper<TerminalInfo> {

    @Update("UPDATE terminal_info SET last_login_time = #{lastLoginTime}, work_total_time = #{workTotalTime}, update_time = NOW() WHERE id = #{id}")
    int updateInfo(@Param("id") int id, @Param("lastLoginTime") Date lastLoginTime, @Param("workTotalTime") int workTotalTime);



}
