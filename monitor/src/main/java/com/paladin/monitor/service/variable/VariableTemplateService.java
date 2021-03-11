package com.paladin.monitor.service.variable;

import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.ServiceSupport;
import com.paladin.framework.utils.convert.SimpleBeanCopyUtil;
import com.paladin.monitor.mapper.variable.VariableTemplateMapper;
import com.paladin.monitor.model.variable.VariableTemplate;
import com.paladin.monitor.service.variable.dto.VariableTemplateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VariableTemplateService extends ServiceSupport<VariableTemplate> {

    @Autowired
    private VariableTemplateMapper variableTemplateMapper;





    @Transactional
    public Boolean updateTemplate(VariableTemplateDTO variableTemplateDTO) {

        VariableTemplate variableTemplate = get(variableTemplateDTO.getId());
        if (variableTemplate == null) {
            throw new BusinessException("找不到需要修改的对象");
        }

        SimpleBeanCopyUtil.simpleCopy(variableTemplateDTO, variableTemplate);
        update(variableTemplate);
        return true;
    }



}
