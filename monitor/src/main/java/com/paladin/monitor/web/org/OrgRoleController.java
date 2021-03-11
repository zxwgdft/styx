package com.paladin.monitor.web.org;

import com.paladin.framework.common.R;
import com.paladin.framework.service.Condition;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.service.QueryType;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.framework.utils.UUIDUtil;
import com.paladin.monitor.core.log.OperationLog;
import com.paladin.monitor.core.security.NeedPermission;
import com.paladin.monitor.model.org.OrgRole;
import com.paladin.monitor.service.org.OrgRolePermissionService;
import com.paladin.monitor.service.org.OrgRoleService;
import com.paladin.monitor.service.org.dto.GrantPermissionDTO;
import com.paladin.monitor.service.org.dto.OrgRoleDTO;
import com.paladin.monitor.service.org.dto.OrgRoleQueryDTO;
import com.paladin.monitor.service.org.vo.OrgRoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/monitor/org/role")
public class OrgRoleController extends ControllerSupport {

    @Autowired
    private OrgRoleService orgRoleService;

    @Autowired
    private OrgRolePermissionService orgRolePermissionService;

    @ApiOperation("角色列表查询")
    @PostMapping("/find/page")
    public PageResult<OrgRoleVO> findPage(@RequestBody OrgRoleQueryDTO query) {
        return orgRoleService.searchPage(query, OrgRoleVO.class);
    }

    @ApiOperation("获取可用角色列表")
    @GetMapping("/find/all/enabled")
    public List<OrgRoleVO> findAllEnabled() {
        return orgRoleService.searchAll(OrgRoleVO.class, new Condition(OrgRole.COLUMN_FIELD_ENABLE, QueryType.EQUAL, 1));
    }

    @ApiOperation("角色详情")
    @GetMapping("/get")
    public OrgRoleVO getDetail(@RequestParam String id) {
        return orgRoleService.get(id, OrgRoleVO.class);
    }

    @ApiOperation("添加角色")
    @PostMapping("/save")
    @NeedPermission("sys:role:add")
    @OperationLog(model = "角色管理", operate = "新增角色")
    public R save(@Valid @RequestBody OrgRoleDTO orgRoleDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        orgRoleDTO.setId(UUIDUtil.createUUID());
        orgRoleService.saveRole(orgRoleDTO);
        return R.success();
    }

    @ApiOperation("更新角色")
    @PostMapping("/update")
    @NeedPermission("sys:role:edit")
    @OperationLog(model = "角色管理", operate = "更新角色")
    public R update(@Valid @RequestBody OrgRoleDTO orgRoleDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        orgRoleService.updateRole(orgRoleDTO);
        return R.success();
    }

    @ApiOperation("角色权限列表")
    @GetMapping("/grant/find/permission")
    public List<String> getGrantAuthorization(@RequestParam String id) {
        return orgRolePermissionService.getPermissionByRole(id);
    }

    @ApiOperation("角色授权")
    @PostMapping("/grant")
    @NeedPermission("sys:role:grant")
    @OperationLog(model = "角色管理", operate = "授权角色")
    public Object grantAuthorization(@RequestBody GrantPermissionDTO grantPermissionDTO) {
        orgRolePermissionService.grantAuthorization(grantPermissionDTO);
        return R.success();
    }
}