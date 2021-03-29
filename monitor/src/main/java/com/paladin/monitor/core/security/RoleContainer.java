package com.paladin.monitor.core.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/3/29
 */
public class RoleContainer {

    private Map<String, Role> roleMap;

    public RoleContainer(List<Role> roles) {
        if (roles == null) {
            roleMap = new HashMap<>();
        } else {
            roleMap = new HashMap<>((int) (roles.size() / 0.75 + 1));
            for (Role role : roles) {
                roleMap.put(role.getId(), role);
            }
        }
    }

    public Role getRole(String roleId) {
        return roleMap.get(roleId);
    }

}
