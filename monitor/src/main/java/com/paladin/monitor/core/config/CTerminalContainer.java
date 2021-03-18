package com.paladin.monitor.core.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.paladin.monitor.core.distrcit.District;
import com.paladin.monitor.core.distrcit.DistrictContainer;
import com.paladin.monitor.model.config.ConfigNode;
import com.paladin.monitor.model.config.ConfigStation;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.service.config.NodeService;
import com.paladin.monitor.service.config.StationService;
import com.paladin.monitor.service.config.TerminalService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 终端、站点、服务节点配置数据
 *
 * @author TontoZhou
 * @since 2020/10/29
 */
@Slf4j
@Component
public class CTerminalContainer extends ConfigContainer {

    @Autowired
    private StationService stationService;

    @Autowired
    private TerminalService stationDeviceService;

    @Autowired
    private NodeService nodeService;

    private List<SimpleDistrict> effectRootDistricts;
    private Map<Integer, SimpleDistrict> effectDistrictMap;
    private Map<String, List<CTerminal>> node2terminalMap;
    private List<SimpleServerNode> serverNodes;
    private Map<Integer, SimpleStation> stationMap;
    private Map<Integer, CTerminal> terminalMap;

    @Override
    public String getId() {
        return ConfigContainer.CONTAINER_TERMINAL;
    }

    @Override
    public void load() {
        List<ConfigStation> stationList = stationService.findList();
        List<ConfigNode> nodeList = nodeService.findList();

        Map<String, List<CTerminal>> node2terminalMap = new HashMap<>();
        List<SimpleServerNode> serverNodes = new ArrayList<>(nodeList.size());

        for (ConfigNode item : nodeList) {
            String code = item.getCode();
            String name = item.getName();
            serverNodes.add(new SimpleServerNode(code, name, null, null));
            node2terminalMap.put(code, new ArrayList<>());
        }

        Map<Integer, SimpleDistrict> rootDistrictMap = new HashMap<>();
        Map<Integer, SimpleDistrict> districtMap = new HashMap<>();
        Map<Integer, SimpleStation> stationMap = new HashMap<>();

        // 生成有效的地区树形结构数据（有站点存在的地区）
        for (ConfigStation item : stationList) {
            District district = DistrictContainer.getDistrict(item.getDistrictCode());
            District city = district.getParent();
            District province = city.getParent();
            SimpleDistrict sp = rootDistrictMap.get(province.getId());
            if (sp == null) {
                sp = new SimpleDistrict(province);
                rootDistrictMap.put(sp.code, sp);
                districtMap.put(sp.code, sp);
            }

            int cityCode = city.getId();
            SimpleDistrict sc = sp.childrenMap.get(cityCode);

            if (sc == null) {
                sc = new SimpleDistrict(city);
                sp.childrenMap.put(cityCode, sc);
                sp.toList();
                districtMap.put(sc.code, sc);
            }

            int districtCode = district.getId();
            SimpleDistrict sd = sc.childrenMap.get(districtCode);
            if (sd == null) {
                sd = new SimpleDistrict(district);
                sc.childrenMap.put(districtCode, sd);
                sc.toList();
                districtMap.put(sd.code, sd);
            }

            int stationId = item.getId();
            stationMap.put(stationId, new SimpleStation(item));
        }

        Map<Integer, CTerminal> terminalMap = new HashMap<>();

        List<ConfigTerminal> deviceList = stationDeviceService.findList();
        for (ConfigTerminal item : deviceList) {
            boolean enabled = item.getEnabled();
            Integer stationId = item.getStationId();
            SimpleStation simpleStation = stationMap.get(stationId);

            // 站点被删除等情况，略过
            if (simpleStation == null) {
                continue;
            }

            // 站点不可用则终端也不可用
            if (!simpleStation.enabled) {
                enabled = false;
            }

            String nodeCode = simpleStation.getNodeCode();
            if (nodeCode == null) {
                // 置空防止null异常
                nodeCode = "";
                // 节点不存在也置为不可用
                enabled = false;
            }

            CTerminal terminal = new CTerminal();
            terminal.setId(item.getId());
            terminal.setName(item.getName());
            terminal.setUid(item.getUid());
            terminal.setStationId(stationId);
            terminal.setStationName(simpleStation.getName());
            terminal.setType(item.getType());
            terminal.setVarIds(item.getVariableIds());
            terminal.setNoAlarmIds(item.getAlarmIds());
            terminal.setNodeCode(nodeCode);
            terminal.setProvinceCode(simpleStation.getProvinceCode());
            terminal.setCityCode(simpleStation.getCityCode());
            terminal.setDistrictCode(simpleStation.getDistrictCode());


            if (enabled) {
                // 服务节点列表只放置启用有效的终端
                List<CTerminal> terminals = node2terminalMap.get(nodeCode);
                if (terminals != null) {
                    terminals.add(terminal);
                } else {
                    // 节点不存在也置为不可用
                    enabled = false;
                    log.warn("站点[ID:" + stationId + "]对应服务节点不存在");
                }
            }

            terminal.setEnabled(enabled);
            terminalMap.put(item.getId(), terminal);
        }


        this.serverNodes = Collections.unmodifiableList(serverNodes);
        this.node2terminalMap = node2terminalMap;
        this.effectDistrictMap = districtMap;
        this.effectRootDistricts = Collections.unmodifiableList(new ArrayList<>(rootDistrictMap.values()));
        this.stationMap = stationMap;
        this.terminalMap = terminalMap;

        log.info("加载服务节点、站点地区、终端配置数据");
    }

    /**
     * 获取服务节点终端列表
     *
     * @param nodeCode 服务节点编码
     * @return 终端列表
     */
    public List<CTerminal> getTerminalOfServerNode(String nodeCode) {
        return node2terminalMap.get(nodeCode);
    }

    /**
     * 获取有效的地区树形结构
     */
    public List<SimpleDistrict> getEffectDistricts() {
        return effectRootDistricts;
    }

    /**
     * 获取某编码有效的地区
     */
    public SimpleDistrict getEffectDistrict(int districtCode) {
        return effectDistrictMap.get(districtCode);
    }

    /**
     * 获取服务节点数据
     */
    public List<SimpleServerNode> getServerNodes() {
        return serverNodes;
    }

    /**
     * 获取父节点编码
     */
    public String getParentServerNodeCode(String nodeCode) {
        if (nodeCode != null && nodeCode.length() > 0) {
            for (SimpleServerNode node : serverNodes) {
                if (node.code.equals(nodeCode)) {
                    return node.parentCode;
                }
            }
        }
        return null;
    }

    /**
     * 获取终端所在节点编码(不存在或不可用的终端会返回null)
     */
    public String getServerNodeOfTerminal(int terminalId) {
        CTerminal terminal = terminalMap.get(terminalId);
        if (terminal != null && terminal.isEnabled()) {
            return terminal.getNodeCode();
        }
        return null;
    }

    /**
     * 获取站点下所有终端
     */
    public List<CTerminal> getTerminalOfStation(int stationId) {
        List<CTerminal> terminals = new ArrayList<>(2);
        for (CTerminal terminal : terminalMap.values()) {
            if (terminal.getStationId() == stationId) {
                terminals.add(terminal);
            }
        }
        return terminals;
    }

    /**
     * 获取终端信息
     */
    public CTerminal getTerminal(int terminalId) {
        return terminalMap.get(terminalId);
    }

    /**
     * 获取所有终端
     */
    public List<CTerminal> getAllTerminals() {
        return new ArrayList<>(terminalMap.values());
    }

    /**
     * 获取站点简单信息
     */
    public SimpleStation getStation(int stationId) {
        return stationMap.get(stationId);
    }

    /**
     * 是否存在节点
     */
    public boolean hasServerNode(String nodeCode) {
        for (SimpleServerNode node : serverNodes) {
            if (node.code.equals(nodeCode)) {
                return true;
            }
        }
        return false;
    }

    @Getter
    @ApiModel
    public static class SimpleDistrict {

        @ApiModelProperty("地区编码")
        private int code;
        @ApiModelProperty("地区名称")
        private String name;

        @JsonIgnore
        private Map<Integer, SimpleDistrict> childrenMap;
        @ApiModelProperty("下级地区")
        private List<SimpleDistrict> children;


        public SimpleDistrict(District district) {
            code = district.getId();
            name = district.getName();
            childrenMap = new HashMap<>();
        }

        private void toList() {
            children = new ArrayList<>(childrenMap.values());
        }

    }

    @Getter
    @Setter
    @ApiModel
    public static class SimpleStation {
        @ApiModelProperty("站点ID")
        private int id;
        @ApiModelProperty("站点名称")
        private String name;
        @ApiModelProperty("省编码")
        private Integer provinceCode;
        @ApiModelProperty("市编码")
        private Integer cityCode;
        @ApiModelProperty("区县编码")
        private int districtCode;
        @ApiModelProperty("服务节点编码")
        private String nodeCode;
        @ApiModelProperty("是否可用")
        private boolean enabled;

        public SimpleStation(ConfigStation item) {
            id = item.getId();
            name = item.getName();
            provinceCode = item.getProvinceCode();
            cityCode = item.getCityCode();
            districtCode = item.getDistrictCode();
            nodeCode = item.getServerNode();
            enabled = item.getEnabled();
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel
    public static class SimpleServerNode {

        @ApiModelProperty("服务节点编码")
        private String code;
        @ApiModelProperty("服务节点名称")
        private String name;
        @ApiModelProperty("父节点编码")
        private String parentCode;

        @ApiModelProperty("子节点")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<SimpleServerNode> child;
    }

}
