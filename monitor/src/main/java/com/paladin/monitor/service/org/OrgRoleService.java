package com.paladin.monitor.service.org;


import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.DataContainerManager;
import com.paladin.framework.service.ServiceSupport;
import com.paladin.framework.utils.convert.SimpleBeanCopyUtil;
import com.paladin.monitor.core.security.RoleContainer;
import com.paladin.monitor.model.org.OrgRole;
import com.paladin.monitor.service.org.dto.OrgRoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrgRoleService extends ServiceSupport<OrgRole> {


    @Autowired
    private DataContainerManager dataContainerManager;

    @Transactional
    public void updateRole(OrgRoleDTO orgRoleDTO) {
        String id = orgRoleDTO.getId();
        if (id == null || id.length() == 0) {
            throw new BusinessException("找不到更新角色");
        }

        OrgRole model = get(id);
        if (model == null) {
            throw new BusinessException("找不到更新角色");
        }

        SimpleBeanCopyUtil.simpleCopy(orgRoleDTO, model);
        model.setRoleLevel(1);
        update(model);
        dataContainerManager.reloadContainer(RoleContainer.class);
    }

    @Transactional
    public void saveRole(OrgRoleDTO orgRoleDTO) {
        OrgRole model = new OrgRole();
        SimpleBeanCopyUtil.simpleCopy(orgRoleDTO, model);
        model.setRoleLevel(1);
        save(model);
        dataContainerManager.reloadContainer(RoleContainer.class);
    }

}