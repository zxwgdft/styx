package com.styx.monitor.core.security;

import com.styx.monitor.mapper.org.OrgRoleMapper;
import com.styx.common.cache.DataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoleDataCache implements DataCache<RoleContainer> {

    @Autowired
    private OrgRoleMapper orgRoleMapper;

    public String getId() {
        return "ROLE_CACHE";
    }

    @Override
    public RoleContainer loadData(long version) {
        return new RoleContainer(orgRoleMapper.findList());
    }
}
