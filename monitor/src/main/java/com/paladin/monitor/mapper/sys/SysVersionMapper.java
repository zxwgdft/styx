package com.paladin.monitor.mapper.sys;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author TontoZhou
 * @since 2020/10/30
 */
public interface SysVersionMapper {

    @Select("SELECT version FROM sys_version WHERE id = #{id}")
    Long getVersion(@Param("id") String id);

    @Update("UPDATE sys_version SET version = #{version} WHERE id = #{id}")
    int updateVersion(@Param("id") String id, @Param("version") long version);

    @Update("UPDATE sys_version SET version = version + 1 WHERE id = #{id}")
    int addVersion(@Param("id") String id);
}
