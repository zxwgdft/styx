package com.styx.monitor.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.monitor.model.config.ConfigTerminal;
import com.styx.monitor.service.config.dto.CTerminal;
import com.styx.monitor.service.config.dto.StationTerminal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigTerminalMapper extends BaseMapper<ConfigTerminal> {

    List<CTerminal> findTerminalConfigList(@Param("serverNode") String serverNode);

    StationTerminal getEnabledStationTerminal(@Param("terminalId") int terminalId);
}
