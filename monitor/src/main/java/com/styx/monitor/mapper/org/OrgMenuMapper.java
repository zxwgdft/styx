package com.styx.monitor.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.monitor.core.security.Menu;
import com.styx.monitor.model.org.OrgMenu;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrgMenuMapper extends BaseMapper<OrgMenu> {

    @Select("SELECT id, `name`, url, icon, parent_id AS parentId FROM org_menu ORDER BY order_no ASC")
    List<Menu> findMenu();
}