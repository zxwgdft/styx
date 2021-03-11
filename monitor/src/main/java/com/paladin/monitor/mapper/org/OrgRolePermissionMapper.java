package com.paladin.monitor.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.model.org.OrgRolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface OrgRolePermissionMapper extends BaseMapper<OrgRolePermission> {

    List<String> getPermissionByRole(@Param("id") String id, @Param("roles") Collection<String> roles);

    int removePermissionByRole(@Param("id") String id);

    int insertByBatch(@Param("roleId") String roleId, @Param("permissionIds") String[] permissionIds);
}