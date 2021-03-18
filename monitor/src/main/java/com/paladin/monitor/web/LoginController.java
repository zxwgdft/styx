package com.paladin.monitor.web;

import com.paladin.monitor.service.sys.AuthService;
import com.paladin.monitor.service.sys.SysUserService;
import com.paladin.monitor.service.sys.dto.LoginSuccess;
import com.paladin.monitor.service.sys.dto.LoginUser;
import com.styx.common.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/monitor")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private AuthService authService;


    @ApiOperation("用户认证")
    @PostMapping("/login")
    public LoginSuccess login(@RequestBody LoginUser loginUser) {
        return authService.auth(loginUser);
    }


    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public R logout() {
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
        } catch (SessionException ise) {
            // don't need care
        }
        return R.success();
    }


}
