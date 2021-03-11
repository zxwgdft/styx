package com.paladin.monitor.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.model.config.ConfigAlarm;
import org.apache.ibatis.annotations.Update;

public interface ConfigAlarmMapper extends BaseMapper<ConfigAlarm> {

    @Update("UPDATE alarm SET enabled = #{enabled} WHERE id = #{id}")
    int updateEnabled(Integer id, boolean enabled);

}
