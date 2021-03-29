package com.paladin.monitor.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.core.security.Role;
import com.paladin.monitor.model.org.OrgRole;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrgRoleMapper extends BaseMapper<OrgRole> {

    @Select("SELECT id, role_name AS name, role_level AS level, enable FROM org_role")
    List<Role> findList();
}