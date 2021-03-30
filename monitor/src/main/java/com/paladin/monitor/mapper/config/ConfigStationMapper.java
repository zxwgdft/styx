package com.paladin.monitor.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.model.config.ConfigStation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ConfigStationMapper extends BaseMapper<ConfigStation> {

    @Update("UPDATE station SET enabled = #{enabled} WHERE id = #{id}")
    int updateStationEnabled(@Param("id") int id, @Param("enabled") boolean enabled);

    @Update("UPDATE station SET server_node = #{node} WHERE id = #{id}")
    int updateStationServerNode(@Param("id") int stationId, @Param("node") String serverNode);

    @Update("UPDATE station SET is_test = 0 WHERE id = #{id}")
    int updateStation2Formal(@Param("id") int stationId);

    @Update("UPDATE station SET order_no = #{orderNo} WHERE id = #{id}")
    int updateStationOrderNo(@Param("id") int stationId, @Param("orderNo") int orderNo);


}
