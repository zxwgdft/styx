package com.paladin.monitor.service.variable;

import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.ServiceSupport;
import com.paladin.framework.utils.convert.JsonUtil;
import com.paladin.monitor.core.MonitorUserSession;
import com.paladin.monitor.mapper.variable.ModuleConfigTemplateMapper;
import com.paladin.monitor.model.variable.ModuleConfigTemplate;
import com.paladin.monitor.service.variable.dto.ModuleConfigTemplateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: cxt
 * @time: 2021/2/2
 */
@Service
@Slf4j
public class ModuleConfigTemplateService extends ServiceSupport<ModuleConfigTemplate> {
    @Autowired
    private ModuleConfigTemplateMapper moduleConfigTemplateMapper;

    /**
     * 保存组态模板
     */
    @Transactional
    public void saveModule(ModuleConfigTemplateDTO dto) {
        String config = "";
        try {
            config = JsonUtil.getJson(dto.getConfig());
        } catch (Exception e) {
            log.error("组态模板配置转换错误:" + e);
        }
        ModuleConfigTemplate moduleConfigTemplate = new ModuleConfigTemplate(null, dto.getName(), dto.getModuleNo(), config);
        MonitorUserSession currentUserSession = MonitorUserSession.getCurrentUserSession();
        String userName = currentUserSession.getUserName();
        moduleConfigTemplate.setDeleted(false);
        moduleConfigTemplate.setCreateBy(userName);
        moduleConfigTemplate.setCreateTime(new Date());
        int insert = this.moduleConfigTemplateMapper.insert(moduleConfigTemplate);
        if (insert != 1) {
            throw new BusinessException("保存失败");
        }
    }

    /**
     * 删除
     */
    public void removeModule(Integer id) {
        int i = this.moduleConfigTemplateMapper.deleteByPrimaryKey(id);
        if (i != 1) {
            throw new BusinessException("删除失败");
        }
    }

    /**
     * 修改
     */
    @Transactional
    public void updateModel(ModuleConfigTemplateDTO dto) {
        String config = "";
        try {
            config = JsonUtil.getJson(dto.getConfig());
        } catch (Exception e) {
            log.error("组态模板配置转换错误:" + e);
        }
        ModuleConfigTemplate moduleConfigTemplate = new ModuleConfigTemplate(dto.getId(), dto.getName(), dto.getModuleNo(), config);
        MonitorUserSession currentUserSession = MonitorUserSession.getCurrentUserSession();
        String userName = currentUserSession.getUserName();
        moduleConfigTemplate.setUpdateBy(userName);
        moduleConfigTemplate.setUpdateTime(new Date());
        int i = this.moduleConfigTemplateMapper.updateByExampleSelective(moduleConfigTemplate, new Example.Builder(ModuleConfigTemplate.class)
                .where(WeekendSqls.<ModuleConfigTemplate>custom().andEqualTo(ModuleConfigTemplate::getId, dto.getId())).build());
        if (i != 1) {
            throw new BusinessException("更新失败");
        }
    }

    /**
     * 分组查询
     * */
    public Map<Integer, List<ModuleConfigTemplate>> grouping(){
        List<ModuleConfigTemplate> moduleConfigTemplates = this.moduleConfigTemplateMapper.selectAll();
        return moduleConfigTemplates.stream().collect(Collectors.groupingBy(ModuleConfigTemplate::getModuleNo));
    }
}
