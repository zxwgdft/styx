package com.styx.data.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
public interface SysMapMapper {

    @Update("UPDATE sys_map_text SET `key` = #{key}, `value`= #{value}, udpate_time = NOW()")
    int putText(@Param("key") String key, @Param("value") String value);

    @Select("SELECT `value` FROM sys_map_text WHERE `key` = #{key}")
    String getText(@Param("key") String key);

}
