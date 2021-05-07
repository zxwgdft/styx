package com.styx.monitor.mapper.config;

import com.styx.common.service.mybatis.CommonMapper;
import com.styx.monitor.model.config.ConfigVariable;
import com.styx.monitor.service.config.cache.SimpleVariable;
import com.styx.monitor.service.config.dto.CVariable;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ConfigVariableMapper extends CommonMapper<ConfigVariable> {

    @Select("SELECT id,`name`,`type`,byte_position bytePosition,bit_position bitPosition,persisted FROM config_variable WHERE deleted = 0 AND enabled = 1")
    List<CVariable> findVariableConfigList();

    @Select("SELECT id,`name`,`type`,unit,`min`,`max`,scale,enabled FROM config_variable WHERE deleted = 0 AND enabled = 1")
    List<SimpleVariable> findEnabledSimpleVariable();
}
