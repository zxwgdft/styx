package com.styx.monitor.mapper.config;

import com.styx.common.service.mybatis.CommonMapper;
import com.styx.monitor.core.security.DataPermissionParam;
import com.styx.monitor.model.config.ConfigStation;
import com.styx.monitor.service.config.dto.StationQuery;
import com.styx.monitor.service.config.vo.SimpleStation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ConfigStationMapper extends CommonMapper<ConfigStation> {

    @Update("UPDATE config_station SET enabled = #{enabled} WHERE id = #{id}")
    int updateStationEnabled(@Param("id") int id, @Param("enabled") boolean enabled);

    @Update("UPDATE config_station SET server_node = #{node} WHERE id = #{id}")
    int updateStationServerNode(@Param("id") int stationId, @Param("node") String serverNode);

    @Update("UPDATE config_station SET order_no = #{orderNo} WHERE id = #{id}")
    int updateStationOrderNo(@Param("id") int stationId, @Param("orderNo") int orderNo);

    List<SimpleStation> findSimpleList(@Param("query") StationQuery query, @Param("permission") DataPermissionParam permission);

    List<ConfigStation> findList(@Param("query") StationQuery query, @Param("permission") DataPermissionParam permission);

}
