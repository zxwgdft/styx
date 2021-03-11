package com.paladin.monitor.service.config;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.paladin.framework.common.R;
import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.Condition;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.service.QueryType;
import com.paladin.framework.service.ServiceSupport;
import com.paladin.framework.utils.convert.SimpleBeanCopyUtil;
import com.paladin.monitor.core.DataPermissionParam;
import com.paladin.monitor.core.DataPermissionUtil;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.config.ConfigContainer;
import com.paladin.monitor.core.config.ConfigContainerManager;
import com.paladin.monitor.mapper.config.ConfigTerminalMapper;
import com.paladin.monitor.model.config.ConfigStation;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.service.config.dto.StationDeviceDTO;
import com.paladin.monitor.service.config.dto.StationDeviceQuery;
import com.paladin.monitor.service.config.vo.Station2Device;
import com.paladin.monitor.service.config.vo.StationDeviceMonitorVO;
import com.paladin.monitor.service.config.vo.StationDeviceSimpleVO;
import com.styx.common.exception.BusinessException;
import com.styx.common.service.ServiceSupport;
import com.styx.common.utils.convert.SimpleBeanCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TerminalService extends ServiceSupport<ConfigTerminal> {

    @Autowired
    private ConfigTerminalMapper stationDeviceMapper;

    @Autowired
    private StationService stationService;

    @Autowired
    private ConfigContainerManager configContainerManager;

    @Autowired
    private CTerminalContainer terminalContainer;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public void saveDevice(StationDeviceDTO stationDeviceDTO) {
        ConfigTerminal model = SimpleBeanCopyUtil.simpleCopy(stationDeviceDTO, new ConfigTerminal());
        model.setId(null);
        save(model);
        configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
    }

    //修改终端信息（参数包括终端id，参数ids）
    @Transactional
    public void updateStationDevice(StationDeviceDTO stationDeviceDTO) {
        ConfigTerminal stationDevice = get(stationDeviceDTO.getId());
        if (stationDevice == null) {
            throw new BusinessException("找不到需要修改的对象");
        }

        SimpleBeanCopyUtil.simpleCopy(stationDeviceDTO, stationDevice);
        if (update(stationDevice)) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
        }
    }

    @Transactional
    public void updateAlarmIds(StationDeviceDTO stationDeviceDTO) {
        ConfigTerminal stationDevice = get(stationDeviceDTO.getId());
        if (stationDevice == null) {
            throw new BusinessException("找不到需要修改的对象");
        }
        stationDevice.setAlarmIds(stationDeviceDTO.getAlarmIds());
        if (update(stationDevice)) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
        }
    }

    @Transactional
    public void removeDevice(int deviceId) {
        ConfigTerminal device = get(deviceId);
        if (device != null) {
            ConfigStation station = stationService.get(device.getStationId());
            if (station != null) {
                Boolean isTest = station.getIsTest();
                if (isTest != null && isTest) {
                    removeDeviceData(String.valueOf(deviceId), station.getServerNode());
                }
            }

            if (stationDeviceMapper.deleteByPrimaryKey(device) > 0) {
                configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
            }
        }
    }

    /**
     * 该方法应该只用于StationService类中的删除站点方法调用
     */
    public void removeDeviceOfStation(ConfigStation station) {
        int stationId = station.getId();
        Boolean isTest = station.getIsTest();

        List<ConfigTerminal> devices = searchAll(
                new Condition(ConfigTerminal.FIELD_STATION_ID, QueryType.EQUAL, stationId)
        );

        if (devices != null && devices.size() > 0) {
            if (isTest != null && isTest) {
                String terminalIds = "";
                for (ConfigTerminal device : devices) {
                    terminalIds += device.getId() + ",";
                }
                removeDeviceData(terminalIds, station.getServerNode());
            }

            for (ConfigTerminal device : devices) {
                stationDeviceMapper.deleteByPrimaryKey(device.getId());
            }
        }
    }

    private void removeDeviceData(String terminalIds, String serverNode) {
        try {
            String url = "http://msms-data-" + serverNode + "/terminal/data/delete/test?terminalIds=" + terminalIds;
            restTemplate.getForEntity(url, R.class);
        } catch (Exception e) {
            log.error("删除数据服务节点[" + serverNode + "]终端数据异常", e);
            throw new BusinessException("删除数据服务节点终端数据失败");
        }
    }


    public List<StationDeviceSimpleVO> findEnabledDevicesByStation(int stationId) {
        return searchAll(
                StationDeviceSimpleVO.class,
                new Condition(ConfigTerminal.FIELD_STATION_ID, QueryType.EQUAL, stationId),
                new Condition(ConfigTerminal.FIELD_ENABLED, QueryType.EQUAL, true)
        );
    }

    public List<ConfigTerminal> findDevicesByStation(int stationId) {
        return searchAll(
                new Condition(ConfigTerminal.FIELD_STATION_ID, QueryType.EQUAL, stationId)
        );
    }


    public PageResult<StationDeviceMonitorVO> findStationDevicePage(StationDeviceQuery query) {
        Page<StationDeviceMonitorVO> page = PageHelper.offsetPage(query.getOffset(), query.getLimit());
        List<StationDeviceMonitorVO> result = findStationDevices(query);
        if (result == null || result.size() == 0) {
            page.setTotal(0L);
        }
        return new PageResult<>(page, result);
    }

    public List<StationDeviceMonitorVO> findStationDevices(StationDeviceQuery query) {
        DataPermissionParam permissionParam = DataPermissionUtil.getUserDataPermission();
        if (permissionParam.isHasPermission()) {
            if (permissionParam.isHasAll()) {
                return stationDeviceMapper.findStationDevice(query, null);
            } else {
                return stationDeviceMapper.findStationDevice(query, permissionParam);
            }
        }
        return new ArrayList<>();
    }

    public PageResult<Integer> findStationDeviceIdPage(StationDeviceQuery query) {
        Page<Integer> page = PageHelper.offsetPage(query.getOffset(), query.getLimit());
        List<Integer> result = findStationDeviceIds(query);
        if (result == null || result.size() == 0) {
            page.setTotal(0L);
        }
        return new PageResult<>(page, result);
    }

    public List<Integer> findStationDeviceIds(StationDeviceQuery query) {
        DataPermissionParam permissionParam = DataPermissionUtil.getUserDataPermission();
        if (permissionParam.isHasPermission()) {
            if (permissionParam.isHasAll()) {
                return stationDeviceMapper.findStationDeviceId(query, null);
            } else {
                return stationDeviceMapper.findStationDeviceId(query, permissionParam);
            }
        }
        return new ArrayList<>();
    }

    public List<Station2Device> findSimpleStationDevices(StationDeviceQuery query) {
        DataPermissionParam permissionParam = DataPermissionUtil.getUserDataPermission();
        if (permissionParam.isHasPermission()) {
            if (permissionParam.isHasAll()) {
                return stationDeviceMapper.findSimpleStationDevice(query, null);
            } else {
                return stationDeviceMapper.findSimpleStationDevice(query, permissionParam);
            }
        }
        return new ArrayList<>();
    }

}
