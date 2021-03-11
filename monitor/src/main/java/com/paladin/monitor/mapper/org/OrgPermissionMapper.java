package com.paladin.monitor.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paladin.monitor.model.org.OrgPermission;
import com.paladin.monitor.service.org.vo.GrantPermissionVO;
import com.paladin.monitor.web.org.vo.OrgPermissionVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrgPermissionMapper extends BaseMapper<OrgPermission> {
    List<OrgPermissionVO> getOrgPermission();

    @Select("SELECT id,`name`,parent_id parentId,is_menu isMenu FROM org_permission WHERE grantable = 1 AND deleted = 0 ORDER BY list_order ASC")
    List<GrantPermissionVO> findPermission4Grant();

    @Select("SELECT id,`name`,parent_id parentId,is_menu isMenu FROM org_permission WHERE deleted = 0 ORDER BY list_order ASC")
    List<GrantPermissionVO> findPermission();
}