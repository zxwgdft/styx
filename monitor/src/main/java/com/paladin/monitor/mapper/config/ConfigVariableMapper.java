package com.paladin.monitor.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.model.config.ConfigVariable;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ConfigVariableMapper extends BaseMapper<ConfigVariable> {

    @Update("UPDATE variable SET enabled = #{enabled} WHERE id = #{id}")
    int updateEnabled(@Param("id") int id, @Param("enabled") boolean enabled);

}
