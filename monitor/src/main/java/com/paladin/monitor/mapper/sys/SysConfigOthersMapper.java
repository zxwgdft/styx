package com.paladin.monitor.mapper.sys;

import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/10/30
 */
public interface SysConfigOthersMapper {

    @Select("SELECT * FROM sys_config_others LIMIT 1")
    Map<String, Object> getOthersConfig();

}
