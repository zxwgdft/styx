package com.paladin.monitor.web.org;

import com.paladin.monitor.core.log.OperationLog;
import com.paladin.monitor.core.security.NeedPermission;
import com.paladin.monitor.model.org.OrgUser;
import com.paladin.monitor.service.org.OrgUserService;
import com.paladin.monitor.service.org.dto.OrgUserDTO;
import com.paladin.monitor.service.org.dto.OrgUserQuery;
import com.styx.common.api.R;
import com.styx.common.service.PageResult;
import com.styx.common.spring.web.ControllerSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = "用户管理")
@RestController
@RequestMapping("/monitor/org/user")
public class OrgUserController extends ControllerSupport {

    @Autowired
    private OrgUserService orgUserService;

    @ApiOperation("用户列表查询")
    @PostMapping("/find/page")
    public PageResult<OrgUser> findPage(@RequestBody OrgUserQuery query) {
        return orgUserService.findPage(query);
    }

    @ApiOperation("用户详情")
    @GetMapping("/get")
    public OrgUser getDetail(@RequestParam String userId) {
        return orgUserService.get(userId);
    }

    @ApiOperation("添加用户")
    @PostMapping("/save")
    @NeedPermission("sys:user:add")
    @OperationLog(model = "用户管理", operate = "新增用户")
    public String save(@Valid @RequestBody OrgUserDTO save, BindingResult bindingResult) {
        validErrorHandler(bindingResult);
        String password = orgUserService.saveUser(save);
        return password;
    }

    @ApiOperation("更新用户")
    @PostMapping("/update")
    @NeedPermission("sys:user:edit")
    @OperationLog(model = "用户管理", operate = "更新用户")
    public R update(@Valid @RequestBody OrgUserDTO update, BindingResult bindingResult) {
        validErrorHandler(bindingResult);
        orgUserService.updateUser(update);
        return R.success();
    }

    @ApiOperation("删除用户")
    @PostMapping("/delete")
    @NeedPermission("sys:user:delete")
    @OperationLog(model = "用户管理", operate = "删除用户")
    public R delete(@RequestParam String userId) {
        orgUserService.removeUser(userId);
        return R.success();
    }

    @ApiOperation("重置密码")
    @GetMapping("/reset")
    @NeedPermission("sys:user:password")
    public String resetPassword(@RequestParam String userId) {
        String password = orgUserService.reset(userId);
        return password;
    }

}
