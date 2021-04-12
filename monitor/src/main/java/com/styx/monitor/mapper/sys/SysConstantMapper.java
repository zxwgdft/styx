package com.styx.monitor.mapper.sys;

import com.styx.monitor.model.sys.SysConstant;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysConstantMapper {

    @Select("SELECT `type`,`code`,`name` FROM sys_constant ORDER BY type ASC,order_no ASC")
    List<SysConstant> findList();
}
