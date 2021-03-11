package com.paladin.monitor.service.org;


import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.DataContainerManager;
import com.paladin.framework.service.ServiceSupport;
import com.paladin.monitor.core.security.Permission;
import com.paladin.monitor.core.security.PermissionContainer;
import com.paladin.monitor.core.security.RoleContainer;
import com.paladin.monitor.mapper.org.OrgRolePermissionMapper;
import com.paladin.monitor.model.org.OrgRolePermission;
import com.paladin.monitor.service.org.dto.GrantPermissionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrgRolePermissionService extends ServiceSupport<OrgRolePermission> {

    @Autowired
    private OrgRolePermissionMapper orgRolePermissionMapper;

    @Autowired
    private DataContainerManager dataContainerManager;

    public List<String> getPermissionByRole(String id) {
        return orgRolePermissionMapper.getPermissionByRole(id, null);
    }

    @Transactional
    public void grantAuthorization(GrantPermissionDTO grantPermissionDTO) {
        String roleId = grantPermissionDTO.getRoleId();
        List<String> permissionIds = grantPermissionDTO.getPermissionIds();

        if (roleId == null || roleId.length() == 0) {
            throw new BusinessException("授权角色ID不能为空");
        }

        orgRolePermissionMapper.removePermissionByRole(roleId);
        if (permissionIds != null && permissionIds.size() > 0) {
            List<String> pids = new ArrayList<>(permissionIds.size());

            HashMap<String, Permission> permissionMap = new HashMap<>();
            for (String pid : permissionIds) {
                Permission permission = PermissionContainer.getPermission(pid);
                if (permission != null) {
                    permissionMap.put(permission.getId(), permission);
                }
            }

            for (Permission permission : permissionMap.values()) {
                if (hasPermission(permission, permissionMap)) {
                    pids.add(permission.getId());
                }
            }

            if (pids.size() > 0) {
                orgRolePermissionMapper.insertByBatch(roleId, pids.toArray(new String[pids.size()]));
            }
        }

        dataContainerManager.reloadContainer(RoleContainer.class);
    }

    /**
     * 如果权限子级存在未授权的子权限，则该权限不应该被授权（基于授权该权限，则其下所有子权限也被授予规则）
     *
     * @param permission
     * @param permissionMap
     * @return
     */
    private boolean hasPermission(Permission permission, Map<String, Permission> permissionMap) {
        if (!permission.isGrantable()) {
            return false;
        }

        if (!permission.isLeaf()) {
            for (Permission child : permission.getChildren()) {
                if (!child.isGrantable()) {
                    continue;
                }

                if (!permissionMap.containsKey(child.getId())) {
                    return false;
                }

                if (!hasPermission(child, permissionMap)) {
                    return false;
                }
            }
        }
        return true;
    }


}