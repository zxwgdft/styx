package com.paladin.monitor.core.security;


import com.paladin.framework.service.DataContainer;
import com.paladin.framework.service.DataContainerManager;
import com.paladin.monitor.model.org.OrgPermission;
import com.paladin.monitor.service.org.OrgPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class PermissionContainer implements DataContainer {

    @Autowired
    private OrgPermissionService orgPermissionService;

    @Autowired
    private DataContainerManager dataContainerManager;

    private static List<Permission> rootPermission;
    private static Map<String, Permission> permissionMap;

    /**
     * 初始化权限
     */
    public void load() {
        log.info("------------初始化权限开始------------");
        Map<String, Permission> permissionMap = new HashMap<>();
        List<Permission> rootPermission = new ArrayList<>();

        List<OrgPermission> orgPermissions = orgPermissionService.findAll();
        for (OrgPermission orgPermission : orgPermissions) {
            permissionMap.put(orgPermission.getId(), new Permission(orgPermission));
        }

        for (Permission permission : permissionMap.values()) {
            OrgPermission source = permission.getSource();
            String parentId = source.getParentId();
            if (parentId != null && parentId.length() > 0) {
                Permission parent = permissionMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(permission);
                    permission.setParent(parent);
                } else {
                    rootPermission.add(permission);
                }
            } else {
                rootPermission.add(permission);
            }
        }

        for (Permission permission : permissionMap.values()) {
            permission.init();
        }

        Collections.sort(rootPermission, (p1, p2) -> {
            return p1.getSource().getListOrder() - p2.getSource().getListOrder();
        });

        PermissionContainer.rootPermission = Collections.unmodifiableList(rootPermission);
        PermissionContainer.permissionMap = Collections.unmodifiableMap(permissionMap);

        log.info("------------初始化权限结束------------");
    }

    public void reload() {
        load();
        dataContainerManager.reloadContainer(RoleContainer.class);
    }


    /**
     * 获取根权限
     */
    public static List<Permission> getRootPermission() {
        return rootPermission;
    }

    /**
     * 获取所有权限
     */
    public static List<Permission> getAllPermission() {
        return new ArrayList<>(permissionMap.values());
    }

    /**
     * 获取权限
     *
     * @param id 权限ID
     */
    public static Permission getPermission(String id) {
        return permissionMap.get(id);
    }


}
