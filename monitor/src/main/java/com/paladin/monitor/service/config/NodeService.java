package com.paladin.monitor.service.config;

import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.config.ConfigContainer;
import com.paladin.monitor.core.config.ConfigContainerManager;
import com.paladin.monitor.model.config.ConfigNode;
import com.paladin.monitor.service.sys.dto.SysNodeDTO;
import com.styx.common.exception.BusinessException;
import com.styx.common.service.ServiceSupport;
import com.styx.common.utils.convert.SimpleBeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeService extends ServiceSupport<ConfigNode> {

    @Autowired
    private ConfigContainerManager configContainerManager;

    @Autowired
    private CTerminalContainer cTerminalContainer;


    public List<CTerminalContainer.SimpleServerNode> getSimpleNodeList() {
        return cTerminalContainer.getServerNodes();
    }

    public void saveNode(SysNodeDTO saveDTO) {
        ConfigNode model = new ConfigNode();
        SimpleBeanCopyUtil.simpleCopy(saveDTO, model);
        model.setIsDefault(false);
        save(model);
        configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
    }

    public void updateNode(SysNodeDTO updateDTO) {
        ConfigNode model = get(updateDTO.getCode());
        if (model == null) {
            throw new BusinessException("找不到更新节点");
        }

        SimpleBeanCopyUtil.simpleCopy(updateDTO, model);

        if (update(model)) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
        }
    }

    public void removeNode(String code) {
        ConfigNode model = get(code);
        if (model == null) {
            throw new BusinessException("找不到需要删除的节点");
        }
        if (model.getIsDefault()) {
            throw new BusinessException("无法删除默认节点");
        }

        List terminals = cTerminalContainer.getTerminalOfServerNode(code);

        if (terminals != null && terminals.size() > 0) {
            throw new BusinessException("该节点下有已经启用的终端，无法删除");
        }

        if (removeById(code)) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_TERMINAL);
        }
    }

    /**
     * 获取父节点
     */
    public List<CTerminalContainer.SimpleServerNode> getParentNode() {
        List<CTerminalContainer.SimpleServerNode> serverNodes = cTerminalContainer.getServerNodes();
        List<CTerminalContainer.SimpleServerNode> collect = serverNodes.stream().filter(e -> e.getParentCode() == null).collect(Collectors.toList());
        for (CTerminalContainer.SimpleServerNode child : collect) {
            child.setChild(getChild(serverNodes, child.getCode()));
        }
        return collect;
    }

    public List<CTerminalContainer.SimpleServerNode> getChild(List<CTerminalContainer.SimpleServerNode> serverNodes, String code) {
        List<CTerminalContainer.SimpleServerNode> list = new ArrayList<>();
        for (CTerminalContainer.SimpleServerNode node : serverNodes) {
            if (code.equals(node.getParentCode())) {
                list.add(node);
            }
        }

        for (CTerminalContainer.SimpleServerNode child : list) {
            child.setChild(getChild(serverNodes, child.getCode()));
        }
        return list;
    }

}