package com.paladin.monitor.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.core.security.Permission;
import com.paladin.monitor.model.org.OrgPermission;
import com.paladin.monitor.service.org.vo.GrantPermissionVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrgPermissionMapper extends BaseMapper<OrgPermission> {

    @Select("SELECT id,`name`,`code` FROM org_permission")
    List<Permission> findPermission();
}