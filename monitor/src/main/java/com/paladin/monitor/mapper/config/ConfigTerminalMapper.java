package com.paladin.monitor.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.core.DataPermissionParam;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.service.data.vo.Device2ServerNode;
import com.paladin.monitor.service.config.dto.TerminalQuery;
import com.paladin.monitor.service.config.vo.Station2Device;
import com.paladin.monitor.service.config.vo.StationDeviceMonitorVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigTerminalMapper extends BaseMapper<ConfigTerminal> {

    List<StationDeviceMonitorVO> findStationDevice(@Param("query") TerminalQuery query, @Param("permission") DataPermissionParam permission);

    List<Integer> findStationDeviceId(@Param("query") TerminalQuery query, @Param("permission") DataPermissionParam permission);

    List<Station2Device> findSimpleStationDevice(@Param("query") TerminalQuery query, @Param("permission") DataPermissionParam permission);

    List<Device2ServerNode> findStationDeviceServer(@Param("query") TerminalQuery query, @Param("permission") DataPermissionParam permission);
}
