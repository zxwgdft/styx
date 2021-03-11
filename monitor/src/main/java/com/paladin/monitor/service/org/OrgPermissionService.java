package com.paladin.monitor.service.org;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.DataContainerManager;
import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.service.ServiceSupport;
import com.paladin.framework.utils.UUIDUtil;
import com.paladin.framework.utils.convert.SimpleBeanCopyUtil;
import com.paladin.monitor.core.security.PermissionContainer;
import com.paladin.monitor.core.security.RoleContainer;
import com.paladin.monitor.mapper.org.OrgPermissionMapper;
import com.paladin.monitor.mapper.org.OrgRolePermissionMapper;
import com.paladin.monitor.model.org.OrgPermission;
import com.paladin.monitor.service.org.dto.OrgPermissionDTO;
import com.paladin.monitor.service.org.vo.GrantPermissionVO;
import com.paladin.monitor.web.org.vo.OrgPermissionTreeVO;
import com.paladin.monitor.web.org.vo.OrgPermissionVO;
import com.styx.common.service.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrgPermissionService extends ServiceSupport<OrgPermission> {

    @Autowired
    private OrgPermissionMapper orgPermissionMapper;

    @Autowired
    private DataContainerManager dataContainerManager;


    /**
     * 分页查询菜单列表
     */
    public PageResult<OrgPermissionVO> getOrgPermission(OffsetPage query) {
        Page<OrgPermissionVO> page = PageHelper.offsetPage(query.getOffset(), query.getLimit());
        this.orgPermissionMapper.getOrgPermission();
        return new PageResult<>(page);
    }

    /**
     * 根据id查询
     */
    public OrgPermission selectById(String id) {
        return this.orgPermissionMapper.selectByPrimaryKey(id);
    }

    /**
     * 更新菜单
     */
    @Transactional
    public Boolean updateOrgPermission(OrgPermissionDTO dto) {
        OrgPermission orgPermission = get(dto.getId());
        if (orgPermission == null) {
            throw new BusinessException("找不到需要修改的对象");
        }
        List<OrgPermission> orgPermissions = selectByExample(new Example.Builder(OrgPermission.class)
                .where(WeekendSqls.<OrgPermission>custom().andNotEqualTo(OrgPermission::getId, dto.getId()).andEqualTo(OrgPermission::getCode, dto.getCode()).andEqualTo(OrgPermission::getDeleted, 0)).build());
        if (orgPermissions.size() != 0) {
            throw new BusinessException("权限code重复");
        }
        SimpleBeanCopyUtil.simpleCopy(dto, orgPermission);
        if (update(orgPermission)) {
            dataContainerManager.reloadContainer(PermissionContainer.class);
        }
        return true;
    }

    /**
     * 新增菜单
     */
    @Transactional
    public Boolean saveOrgPermission(OrgPermissionDTO dto) {
        List<OrgPermission> orgPermissions = selectByExample(new Example.Builder(OrgPermission.class)
                .where(WeekendSqls.<OrgPermission>custom().andEqualTo(OrgPermission::getCode, dto.getCode()).andEqualTo(OrgPermission::getDeleted, 0)).build());
        if (orgPermissions.size() != 0) {
            throw new BusinessException("权限code重复");
        }
        dto.setId(UUIDUtil.createUUID());
        OrgPermission orgPermission = new OrgPermission();
        SimpleBeanCopyUtil.simpleCopy(dto, orgPermission);
        if (save(orgPermission)) {
            dataContainerManager.reloadContainer(PermissionContainer.class);
        }
        return true;
    }

    /**
     * 删除菜单
     */
    public Boolean removeOrgPermission(String id) {
        if (orgPermissionMapper.deleteByPrimaryKey(id) > 0) {
            dataContainerManager.reloadContainer(PermissionContainer.class);
        }
        return true;
    }

    public List<GrantPermissionVO> findPermission4Grant() {
        return orgPermissionMapper.findPermission4Grant();
    }

    public List<GrantPermissionVO> findPermission() {
        return orgPermissionMapper.findPermission();
    }
}