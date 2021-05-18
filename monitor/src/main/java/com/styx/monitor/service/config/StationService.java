package com.styx.monitor.service.config;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.styx.common.service.PageResult;
import com.styx.monitor.core.MonitorServiceSupport;
import com.styx.monitor.core.distrcit.DistrictUtil;
import com.styx.monitor.core.security.DataPermissionParam;
import com.styx.monitor.core.security.PermissionUtil;
import com.styx.monitor.mapper.config.ConfigStationMapper;
import com.styx.monitor.model.config.ConfigStation;
import com.styx.monitor.service.config.dto.StationQuery;
import com.styx.monitor.service.config.vo.SimpleStationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StationService extends MonitorServiceSupport<ConfigStation, ConfigStationMapper> {

    public List<SimpleStationVO> findSimpleList(StationQuery query) {
        DistrictUtil.perfectDistrictQuery(query);
        DataPermissionParam permissionParam = PermissionUtil.getUserDataPermission();
        return getSqlMapper().findSimpleList(query, permissionParam);
    }

    public PageResult<ConfigStation> findStationPage(StationQuery query) {
        DistrictUtil.perfectDistrictQuery(query);
        DataPermissionParam permissionParam = PermissionUtil.getUserDataPermission();
        Page<ConfigStation> page = PageHelper.offsetPage(query.getOffset(), query.getLimit());
        List<ConfigStation> result = getSqlMapper().findList(query, permissionParam);
        return new PageResult<>(page, result);
    }

}
