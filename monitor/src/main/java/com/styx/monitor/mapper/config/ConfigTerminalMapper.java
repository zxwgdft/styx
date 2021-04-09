package com.styx.monitor.mapper.config;

import com.styx.common.service.mybatis.CommonMapper;
import com.styx.monitor.model.config.ConfigTerminal;
import com.styx.monitor.service.config.dto.CTerminal;
import com.styx.monitor.service.config.dto.StationTerminal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigTerminalMapper extends CommonMapper<ConfigTerminal> {

    List<CTerminal> findTerminalConfigList(@Param("serverNode") String serverNode);

    StationTerminal getEnabledStationTerminal(@Param("terminalId") int terminalId);
}
