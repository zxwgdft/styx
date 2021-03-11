package com.paladin.monitor.web.org;

import com.paladin.framework.common.R;
import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.core.MonitorUserSession;
import com.paladin.monitor.core.security.Menu;
import com.paladin.monitor.model.org.OrgPermission;
import com.paladin.monitor.service.org.OrgPermissionService;
import com.paladin.monitor.service.org.dto.OrgPermissionDTO;
import com.paladin.monitor.service.org.vo.GrantPermissionVO;
import com.paladin.monitor.web.org.vo.OrgPermissionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author cxt
 * @date 2020/11/9
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/monitor/org/permission")
public class OrgPermissionController extends ControllerSupport {
    @Autowired
    private OrgPermissionService orgPermissionService;


    @ApiOperation("查询菜单")
    @GetMapping("/get")
    public PageResult<OrgPermissionVO> getOrgPermission(OffsetPage query) {
        return orgPermissionService.getOrgPermission(query);
    }

    @ApiOperation("查询菜单详情")
    @GetMapping("/getDetail")
    public OrgPermission getDetail(@RequestParam("id") String id) {
        return orgPermissionService.selectById(id);
    }

    @ApiOperation("修改菜单")
    @PostMapping("/update")
    public R update(@Valid @RequestBody OrgPermissionDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        return orgPermissionService.updateOrgPermission(dto) ? R.success() : R.fail("更新失败");
    }

    @ApiOperation("删除菜单")
    @GetMapping("/remove/{id}")
    public R remove(@PathVariable("id") String id) {
        return orgPermissionService.removeOrgPermission(id) ? R.success() : R.fail("删除失败");
    }

    @ApiOperation("新增菜单")
    @PostMapping("/save")
    public R save(@Valid @RequestBody OrgPermissionDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        return this.orgPermissionService.saveOrgPermission(dto) ? R.success() : R.fail("保存失败");
    }

    @ApiOperation("查找所有可授权权限")
    @GetMapping("/find/grant")
    public List<GrantPermissionVO> find4Grant() {
        return this.orgPermissionService.findPermission4Grant();
    }

    @ApiOperation("查找所有权限")
    @GetMapping("/find/all")
    public List<GrantPermissionVO> findAll() {
        return this.orgPermissionService.findPermission();
    }

    @ApiOperation("根据权限查询菜单列表")
    @GetMapping("/getMenu")
    public List<Menu> getMenu() {
        return MonitorUserSession.getCurrentUserSession().getMenuResources();
    }
}
