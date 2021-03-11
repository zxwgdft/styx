package com.paladin.monitor.service.variable;

import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.ServiceSupport;
import com.paladin.framework.utils.convert.JsonUtil;
import com.paladin.monitor.mapper.variable.ModuleConfigMapper;
import com.paladin.monitor.model.variable.ModuleConfig;
import com.paladin.monitor.service.variable.dto.ModuleConfigDTO;
import com.paladin.monitor.service.variable.dto.ModuleConfigItem;
import com.paladin.monitor.service.variable.vo.ModuleConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModuleConfigService extends ServiceSupport<ModuleConfig> {

    @Autowired
    private ModuleConfigMapper moduleConfigMapper;

    // 默认配置的终端ID
    private int default_config_terminal_id = -1;


    public List<ModuleConfigVO> findModuleConfigOfTerminal(int terminalId) {
        return moduleConfigMapper.findModuleConfigOfTerminal(terminalId);
    }


    @Transactional
    public void updateModuleConfig(ModuleConfigDTO moduleConfig) {

        int terminalId = moduleConfig.getTerminalId();

        // 删除所有终端模组配置
        moduleConfigMapper.removeModuleConfig(terminalId);

        List<ModuleConfigItem> items = moduleConfig.getConfigs();
        if (items != null && items.size() > 0) {
            for (ModuleConfigItem item : items) {

                int moduleNo = item.getModuleNo();

                Integer[][] config = item.getConfig();
                if (!checkConfig(moduleNo, config)) {
                    throw new BusinessException("模组配置参数非法");
                }

                ModuleConfig pk = new ModuleConfig();
                pk.setTerminalId(terminalId);
                pk.setModuleNo(moduleNo);

                try {
                    pk.setConfig(JsonUtil.getJson(config));
                } catch (Exception e) {
                    //
                }

                save(pk);
            }
        }
    }

    private boolean checkConfig(int moduleNo, Integer[][] config) {
        if (config == null) return false;
        // 暂时不做详细校验
        return true;
    }


    public ModuleConfigVO getModuleConfig(int terminalId, int moduleNo) {
        ModuleConfigVO moduleConfig = moduleConfigMapper.getModuleConfigOfTerminal(terminalId, moduleNo);
        if (moduleConfig == null) {
            moduleConfig = moduleConfigMapper.getModuleConfigOfTerminal(default_config_terminal_id, moduleNo);
        }
        return moduleConfig;
    }
}