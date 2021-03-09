package com.styx.data.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author TontoZhou
 * @since 2020/12/23
 */
public interface SysMapMapper {

    @Select("SELECT `value` FROM sys_map WHERE `key` = 'data_upload_index'")
    String getDataUploadIndex();

    @Update("UPDATE sys_map SET `value` = #{index} WHERE `key` = 'data_upload_index'")
    int updateDataUploadIndex(@Param("index") String dataUploadIndex);

    @Insert("INSERT sys_map VALUES ('data_upload_index', #{index})")
    int insertDataUploadIndex(@Param("index") String dataUploadIndex);
}
