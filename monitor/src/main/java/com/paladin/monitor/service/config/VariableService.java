package com.paladin.monitor.service.config;

import com.paladin.monitor.core.config.*;
import com.paladin.monitor.mapper.config.ConfigVariableMapper;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.model.config.ConfigVariable;
import com.paladin.monitor.service.config.dto.VariableDTO;
import com.paladin.monitor.service.config.vo.VariableVO;
import com.styx.common.exception.BusinessException;
import com.styx.common.service.ServiceSupport;
import com.styx.common.utils.convert.SimpleBeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VariableService extends ServiceSupport<ConfigVariable> {

    @Autowired
    private ConfigVariableMapper variableMapper;

    @Autowired
    private ConfigContainerManager configContainerManager;

    @Autowired
    private CAlarmContainer alarmContainer;

    @Autowired
    private CVariableContainer variableContainer;

    @Autowired
    private TerminalService stationDeviceService;


    public List<VariableVO> getShowedVariables() {
        return variableContainer.getShowVariables();
    }

    public List<VariableVO> getShowedVariablesOfTerminal(int terminalId) {
        ConfigTerminal terminal = stationDeviceService.get(terminalId);
        if (terminal == null) {
            throw new BusinessException("终端不存在");
        }

        String vids = terminal.getVariableIds();

        List<VariableVO> all = variableContainer.getShowVariables();
        List<VariableVO> list = new ArrayList<>(all.size());

        if (vids != null && vids.length() > 0) {
            String[] arr = vids.split(",");
            Set<String> set = new HashSet<>();
            for (String str : arr) {
                set.add(str);
            }

            for (VariableVO var : all) {
                if (set.contains(String.valueOf(var.getId()))) {
                    list.add(var);
                }
            }
        }
        return list;
    }

    @Transactional
    public void saveVariable(VariableDTO variableDTO) {
        ConfigVariable variable = SimpleBeanCopyUtil.simpleCopy(variableDTO, new ConfigVariable());
        variable.setId(null);
        save(variable);
        configContainerManager.reloadContainer(ConfigContainer.CONTAINER_VARIABLE);
    }

    @Transactional
    public void updateVariable(VariableDTO model) {
        ConfigVariable variable = get(model.getId());
        if (variable == null) {
            throw new BusinessException("找不到需要修改的对象");
        }

        if (!model.getEnabled() && checkUsed(model.getId())) {
            throw new BusinessException("报警中有使用到该变量,不能停用该变量！");
        }

        SimpleBeanCopyUtil.simpleCopy(model, variable);
        update(variable);
        configContainerManager.reloadContainer(ConfigContainer.CONTAINER_VARIABLE);
    }

    @Transactional
    public void startVariable(int id) {
        if (variableMapper.updateEnabled(id, true) > 0) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_VARIABLE);
        }
    }

    @Transactional
    public void stopVariable(int id) {
        if (checkUsed(id)) {
            throw new BusinessException("报警中有使用到该变量,不能停用该变量！");
        }

        if (variableMapper.updateEnabled(id, false) > 0) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_VARIABLE);
        }
    }


    public void removeVariable(int id) {
        if (checkUsed(id)) {
            throw new BusinessException("报警中有使用到该变量,不能删除该变量！");
        }
        if (removeById(id)) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_VARIABLE);
        }
    }

    private boolean checkUsed(int id) {
        String idStr = id + ",";
        for (CAlarm alarm : alarmContainer.getEnabledAlarms()) {
            String str = alarm.getVariableIds();
            if (str != null) {
                str += ",";
                if ((str + ",").indexOf(idStr) > -1) {
                    return true;
                }
            }
        }
        return false;
    }


}
