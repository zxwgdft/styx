package com.paladin.monitor.service.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.config.ConfigContainer;
import com.paladin.monitor.core.config.ConfigContainerManager;
import com.paladin.monitor.mapper.config.ConfigTerminalMapper;
import com.paladin.monitor.model.config.ConfigStation;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.service.config.dto.StationDeviceDTO;
import com.styx.common.api.R;
import com.styx.common.exception.BusinessException;
import com.styx.common.service.ServiceSupport;
import com.styx.common.utils.convert.SimpleBeanCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class TerminalService extends ServiceSupport<ConfigTerminal> {

    @Autowired
    private ConfigTerminalMapper terminalMapper;

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
            if (removeById(deviceId)) {
                configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
            }
        }
    }

    /**
     * 该方法应该只用于StationService类中的删除站点方法调用
     */
    public void removeDeviceOfStation(ConfigStation station) {
        int stationId = station.getId();

        List<ConfigTerminal> devices = findList(
                new LambdaQueryWrapper<ConfigTerminal>()
                        .eq(ConfigTerminal::getStationId, stationId)
        );

        if (devices != null && devices.size() > 0) {
            for (ConfigTerminal device : devices) {
                removeById(device.getId());
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


}
