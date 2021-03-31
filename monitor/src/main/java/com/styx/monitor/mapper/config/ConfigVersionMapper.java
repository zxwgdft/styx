package com.styx.monitor.mapper.config;

import com.styx.monitor.model.config.ConfigVersion;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/10/30
 */
public interface ConfigVersionMapper {

    @Select("SELECT version FROM config_version WHERE id = #{id}")
    Long getVersion(@Param("id") String id);

    @Select("SELECT id, version FROM config_version")
    List<ConfigVersion> getVersionList();

    @Update("UPDATE config_version SET version = #{version} WHERE id = #{id}")
    int updateVersion(@Param("id") String id, @Param("version") long version);

    @Update("UPDATE config_version SET version = version + 1 WHERE id = #{id}")
    int addVersion(@Param("id") String id);

    @Insert("INSERT INTO config_version VALUES(#{id},#{version})")
    int insertVersion(@Param("id") String id, @Param("version") long version);
}
