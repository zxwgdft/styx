package com.paladin.monitor.service.sys;

import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.DataContainerManager;
import com.paladin.framework.service.ServiceSupport;
import com.paladin.framework.utils.convert.SimpleBeanCopyUtil;
import com.paladin.monitor.core.Dictionary;
import com.paladin.monitor.mapper.sys.SysConstantMapper;
import com.paladin.monitor.model.sys.SysConstant;
import com.paladin.monitor.service.sys.dto.SysConstantDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysConstantService extends ServiceSupport<SysConstant> {

    @Autowired
    private SysConstantMapper sysConstantMapper;

    @Autowired
    private DataContainerManager dataContainerManager;

    @Transactional
    public void saveSysConstant(SysConstantDTO sysConstantDTO) {
        SysConstant model = SimpleBeanCopyUtil.simpleCopy(sysConstantDTO, new SysConstant());
        valid(model);
        save(model);
        dataContainerManager.reloadContainer(Dictionary.class);
    }

    @Transactional
    public void updateSysConstant(SysConstantDTO model) {
        SysConstant sysConstant = this.queryEntityByTypeAndCode(model.getType(), model.getCode());
        if (sysConstant == null) {
            throw new BusinessException("找不到需要修改的对象");
        }
        SimpleBeanCopyUtil.simpleCopy(model, sysConstant);
        update(sysConstant);
        dataContainerManager.reloadContainer(Dictionary.class);
    }

    public SysConstant queryEntityByTypeAndCode(String type, String code) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(code)) {
            return null;
        }
        return sysConstantMapper.queryEntityByTypeAndCode(type, code);
    }

    @Transactional
    public void removeSysConstant(String type, String code) {
        SysConstant dbEntity = this.queryEntityByTypeAndCode(type, code);
        if (dbEntity == null) {
            throw new BusinessException("数据库中不存在该记录！");
        }
        removeByPrimaryKey(dbEntity);
        dataContainerManager.reloadContainer(Dictionary.class);
    }


    private void valid(SysConstant model) {
        SysConstant dbEntity = this.queryEntityByTypeAndCode(model.getType(), model.getCode());
        if (dbEntity != null) {
            throw new BusinessException("数据库中已存在类型为" + model.getType() + "，code为" + model.getCode() + "的变量，不可重复添加！");
        }
    }

    public List<SysConstant> findByTypeArr(String[] type) {
        return sysConstantMapper.findByTypeArr(type);
    }
}
