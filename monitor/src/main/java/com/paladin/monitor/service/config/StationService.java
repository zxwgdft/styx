package com.paladin.monitor.service.config;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.paladin.monitor.core.DataPermissionParam;
import com.paladin.monitor.core.DataPermissionUtil;
import com.paladin.monitor.core.MonitorUserSession;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.config.ConfigContainer;
import com.paladin.monitor.core.config.ConfigContainerManager;
import com.paladin.monitor.core.distrcit.District;
import com.paladin.monitor.core.distrcit.DistrictContainer;
import com.paladin.monitor.mapper.config.ConfigStationMapper;
import com.paladin.monitor.model.config.ConfigStation;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.service.config.dto.StationDTO;
import com.paladin.monitor.service.config.dto.StationEditDTO;
import com.paladin.monitor.service.config.dto.StationQuery;
import com.paladin.monitor.service.config.vo.SimpleStationVO;
import com.paladin.monitor.service.config.vo.StationVO;
import com.styx.common.api.R;
import com.styx.common.exception.BusinessException;
import com.styx.common.service.PageResult;
import com.styx.common.service.ServiceSupport;
import com.styx.common.utils.convert.SimpleBeanCopyUtil;
import com.styx.common.utils.others.PinyinUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StationService extends ServiceSupport<ConfigStation> {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigStationMapper stationMapper;

    @Autowired
    private TerminalService stationDeviceService;

    @Autowired
    private CTerminalContainer terminalContainer;

    @Autowired
    private ConfigContainerManager configContainerManager;


    @Transactional
    public void saveStation(StationDTO stationDTO) {
        ConfigStation model = new ConfigStation();
        SimpleBeanCopyUtil.simpleCopy(stationDTO, model);
        model.setId(null);
        checkStation(model);

        save(model);
        configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
    }

    @Transactional
    public void updateStation(StationEditDTO stationDTO) {
        ConfigStation model = get(stationDTO.getId());
        if (model == null) {
            throw new BusinessException("找不到需要修改的站点");
        }

        boolean needUpdate = false;
        if (
                !model.getDistrictCode().equals(stationDTO.getDistrictCode())
                        || !model.getName().equals(stationDTO.getName())
                        || !model.getEnabled().equals(stationDTO.getEnabled())
        ) {
            // 判断是否关键字段修改，如果修改需要更新配置，不重要的字段修改不需要重新读取配置
            needUpdate = true;
        }

        SimpleBeanCopyUtil.simpleCopy(stationDTO, model);
        checkStation(model);
        update(model);
        if (needUpdate) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
        }
    }

    private void checkStation(ConfigStation station) {
        //根据地图信息获取区县code，再获取省市code
        Integer districtCode = station.getDistrictCode();
        District district = DistrictContainer.getDistrict(districtCode);
        if (district == null) {
            throw new BusinessException("找不到编码为[" + districtCode + "]的地区");
        }
        int level = district.getLevel();

        if (level != 3) {
            throw new BusinessException("请选择该站点所属区县位置");
        } else {
            station.setProvinceCode(district.getParent().getParent().getId());
            station.setCityCode(district.getParent().getId());
            station.setDistrictCode(districtCode);
        }

        String stationName = station.getName();
        String pinyinName = PinyinUtil.toHanyuPinyinFirstArray(stationName);
        station.setPinyinName(pinyinName);
    }

    @Transactional
    public void updateStationEnabled(int id, boolean enabled) {
        if (stationMapper.updateStationEnabled(id, enabled) > 0) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
        }
    }

    @Transactional
    public void removeStation(int id) {
        ConfigStation station = get(id);
        if (station != null) {
            stationDeviceService.removeDeviceOfStation(station);
            removeById(id);
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
        }
    }

    public List<CTerminalContainer.SimpleDistrict> getEffectDistricts() {
        return terminalContainer.getEffectDistricts();
    }

    public PageResult<StationVO> findStationPage(StationQuery query) {
        // 增加权限查询条件
        DataPermissionParam permissionParam = DataPermissionUtil.getUserDataPermission();
        if (permissionParam.isHasPermission()) {
            Page<StationVO> page = PageHelper.offsetPage(query.getOffset(), query.getLimit());
            List<StationVO> result = permissionParam.isHasAll() ? stationMapper.findStation(query, null) :
                    stationMapper.findStation(query, permissionParam);
            if (result == null || result.size() == 0) {
                page.setTotal(0L);
            }
            return new PageResult<>(page, result);
        }
        return PageResult.getEmptyPageResult(query.getLimit());
    }


    public List<SimpleStationVO> findSimpleStations(StationQuery query) {
        // 增加权限查询条件
        DataPermissionParam permissionParam = DataPermissionUtil.getUserDataPermission();
        if (permissionParam.isHasPermission()) {
            if (permissionParam.isHasPermission()) {
                return permissionParam.isHasAll() ? stationMapper.findSimpleStation(query, null) :
                        stationMapper.findSimpleStation(query, permissionParam);
            }
        }

        return new ArrayList<>();
    }


    public void changeStationServerNode(int stationId, String targetNode) {
        ConfigStation station = get(stationId);
        if (station == null) {
            throw new BusinessException("站点不存在");
        }

        if (targetNode == null || targetNode.length() == 0 || !terminalContainer.hasServerNode(targetNode)) {
            throw new BusinessException("转移目标节点不能为空或不存在");
        }

        if (!DataPermissionUtil.hasStationPermission(MonitorUserSession.getCurrentUserSession(), station)) {
            throw new BusinessException("没有权限转移站点终端");
        }

        if (targetNode.equals(station.getServerNode())) {
            return;
        }

        List<ConfigTerminal> stationDevices = stationDeviceService.findDevicesByStation(stationId);
        if (stationDevices != null && stationDevices.size() > 0) {

            String terminalIds = "";
            for (ConfigTerminal sd : stationDevices) {
                terminalIds += sd.getId() + ",";
            }

            String serverNode = station.getServerNode();
            try {
                String url = "http://msms-data-" + serverNode + "/terminal/control/move?terminalIds=" + terminalIds + "&targetNode=" + targetNode;
                restTemplate.getForEntity(url, R.class);
            } catch (Exception e) {
                log.error("转移站点[" + stationId + "]终端到节点[" + targetNode + "]异常", e);
                throw new BusinessException("转移站点终端到节点异常");
            }
        }

        stationMapper.updateStationServerNode(stationId, targetNode);
        configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
    }

    public void turnFormalStation(int stationId) {
        ConfigStation station = get(stationId);
        if (station == null) {
            throw new BusinessException("站点不存在");
        }

        if (!DataPermissionUtil.hasStationPermission(MonitorUserSession.getCurrentUserSession(), station)) {
            throw new BusinessException("没有权限转移站点终端");
        }

        List<ConfigTerminal> stationDevices = stationDeviceService.findDevicesByStation(stationId);
        if (stationDevices != null && stationDevices.size() > 0) {

            String terminalIds = "";
            for (ConfigTerminal sd : stationDevices) {
                terminalIds += sd.getId() + ",";
            }

            String serverNode = station.getServerNode();
            try {
                String url = "http://msms-data-" + serverNode + "/terminal/control/turn/formal?terminalIds=" + terminalIds;
                restTemplate.getForEntity(url, R.class);
            } catch (Exception e) {
                log.error("测试站点[" + stationId + "]终端转为正式异常", e);
                throw new BusinessException("测试站点终端转为正式异常");
            }
        }


        stationMapper.updateStation2Formal(stationId);
        configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
    }

    public void uploadStationOrderNo(int stationId, int orderNo) {
        stationMapper.updateStationOrderNo(stationId, orderNo);
    }


}
