package com.styx.monitor.mapper.org;

import com.styx.monitor.model.org.OrgRolePermission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

public interface OrgRolePermissionMapper {

    List<String> getPermissionByRole(@Param("id") String id, @Param("roles") Collection<String> roles);

    int removePermissionByRole(@Param("id") String id);

    int insertByBatch(@Param("roleId") String roleId, @Param("permissionIds") String[] permissionIds);

    @Select("SELECT role_id AS roleId, permission_id AS permissionId FROM org_role_permission ORDER BY role_id")
    List<OrgRolePermission> findList();
}