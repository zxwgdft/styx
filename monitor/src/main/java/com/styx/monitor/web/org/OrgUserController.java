package com.styx.monitor.web.org;

import com.styx.monitor.core.log.OperationLog;
import com.styx.monitor.core.security.NeedPermission;
import com.styx.monitor.model.org.OrgUser;
import com.styx.monitor.service.org.OrgUserService;
import com.styx.monitor.service.org.dto.OrgUserDTO;
import com.styx.monitor.service.org.dto.OrgUserQuery;
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
    @OperationLog(model = "用户管理", operate = "新增用户")
    public String save(@Valid @RequestBody OrgUserDTO save, BindingResult bindingResult) {
        validErrorHandler(bindingResult);
        String password = orgUserService.saveUser(save);
        return password;
    }

    @ApiOperation("更新用户")
    @PostMapping("/update")
    @OperationLog(model = "用户管理", operate = "更新用户")
    public R update(@Valid @RequestBody OrgUserDTO update, BindingResult bindingResult) {
        validErrorHandler(bindingResult);
        orgUserService.updateUser(update);
        return R.success();
    }

   
}
