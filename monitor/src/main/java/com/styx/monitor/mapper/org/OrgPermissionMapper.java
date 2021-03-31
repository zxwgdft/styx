package com.styx.monitor.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.monitor.core.security.Permission;
import com.styx.monitor.model.org.OrgPermission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrgPermissionMapper extends BaseMapper<OrgPermission> {

    @Select("SELECT id,`name`,`code` FROM org_permission")
    List<Permission> findPermission();
}