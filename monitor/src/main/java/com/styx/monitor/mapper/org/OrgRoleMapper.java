package com.styx.monitor.mapper.org;

import com.styx.common.service.mybatis.CommonMapper;
import com.styx.monitor.core.security.Role;
import com.styx.monitor.model.org.OrgRole;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrgRoleMapper extends CommonMapper<OrgRole> {

    @Select("SELECT id, role_name AS name, role_level AS level, enable FROM org_role")
    List<Role> findList();
}