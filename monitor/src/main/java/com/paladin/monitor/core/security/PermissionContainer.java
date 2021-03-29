package com.paladin.monitor.core.security;

import java.util.Map;
import java.util.Set;

/**
 * @author TontoZhou
 * @since 2021/3/23
 */
public class PermissionContainer {

    private Map<String, Set<String>> role2PermissionCodeMap;

    public PermissionContainer(Map<String, Set<String>> map) {
        this.role2PermissionCodeMap = map;
    }

    public Set<String> getPermissionCodeByRole(String roleId) {
        return role2PermissionCodeMap.get(roleId);
    }

    public boolean hasPermission(String roleId, String code) {
        Set<String> codes = role2PermissionCodeMap.get(roleId);
        return codes != null && codes.contains(code);
    }

}
