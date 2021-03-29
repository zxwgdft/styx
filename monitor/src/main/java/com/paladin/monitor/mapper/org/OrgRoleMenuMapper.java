package com.paladin.monitor.mapper.org;

import com.paladin.monitor.model.org.OrgRoleMenu;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/3/25
 */
public interface OrgRoleMenuMapper {
    @Select("SELECT role_id AS roleId, menu_id AS menuId FROM org_role_menu")
    List<OrgRoleMenu> findList();
}
