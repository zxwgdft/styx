package com.paladin.monitor.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.model.config.ConfigVariable;
import com.paladin.monitor.service.config.dto.CVariable;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ConfigVariableMapper extends BaseMapper<ConfigVariable> {

    @Select("SELECT id,`name`,`type`,byte_position bytePosition,bit_position bitPosition,persisted FROM config_variable WHERE deleted = 0 AND enabled = 1")
    List<CVariable> findVariableConfigList();
}
