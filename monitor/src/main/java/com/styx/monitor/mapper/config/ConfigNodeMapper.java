package com.styx.monitor.mapper.config;

import com.styx.common.service.mybatis.CommonMapper;
import com.styx.monitor.model.config.ConfigNode;
import com.styx.monitor.service.data.vo.NodeReaData;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ConfigNodeMapper extends CommonMapper<ConfigNode> {

    @Select("SELECT `code`,`name` FROM config_node")
    List<NodeReaData> findRealList();
}
