package com.styx.data.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
public interface SysMapMapper {

    @Update("UPDATE sys_map_text SET `value`= #{value}, update_time = NOW() WHERE `key` = #{key}")
    int updateTextValue(@Param("key") String key, @Param("value") String value);

    @Insert("INSERT INTO sys_map_text (`key`,`value`,update_time) VALUES (#{key}, #{value}, NOW())")
    int insertText(@Param("key") String key, @Param("value") String value);

    @Select("SELECT `value` FROM sys_map_text WHERE `key` = #{key}")
    String getTextValue(@Param("key") String key);

}
