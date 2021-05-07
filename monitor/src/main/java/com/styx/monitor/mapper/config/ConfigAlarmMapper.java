package com.styx.monitor.mapper.config;

import com.styx.common.service.mybatis.CommonMapper;
import com.styx.monitor.model.config.ConfigAlarm;
import com.styx.monitor.service.config.cache.SimpleAlarm;
import com.styx.monitor.service.config.dto.CAlarm;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ConfigAlarmMapper extends CommonMapper<ConfigAlarm> {

    @Update("UPDATE alarm SET enabled = #{enabled} WHERE id = #{id}")
    int updateEnabled(Integer id, boolean enabled);

    @Select("SELECT id, `name`, express formula, variable_list variableIds FROM config_alarm WHERE deleted = 0 AND enabled = 1")
    List<CAlarm> findAlarmConfigList();

    @Select("SELECT id, `name`, enabled FROM config_alarm WHERE deleted = 0 AND enabled = 1")
    List<SimpleAlarm> findEnableSimpleAlarm();
}
