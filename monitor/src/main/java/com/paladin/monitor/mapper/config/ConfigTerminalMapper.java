package com.paladin.monitor.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.service.config.dto.CTerminal;
import com.paladin.monitor.service.config.dto.StationTerminal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigTerminalMapper extends BaseMapper<ConfigTerminal> {

    List<CTerminal> findTerminalConfigList(@Param("serverNode") String serverNode);

    StationTerminal getEnabledStationTerminal(@Param("terminalId") int terminalId);
}
