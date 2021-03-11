package com.paladin.monitor.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.model.sys.SysConstant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysConstantMapper extends BaseMapper<SysConstant> {

    SysConstant queryEntityByTypeAndCode(@Param("type") String type, @Param("code") String code);

    List<SysConstant> findByTypeArr(@Param("typeList") String[] type);
}
