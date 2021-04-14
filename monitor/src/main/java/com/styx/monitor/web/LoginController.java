package com.styx.monitor.web;

import com.styx.common.api.R;
import com.styx.monitor.service.sys.AuthService;
import com.styx.monitor.service.sys.dto.LoginResult;
import com.styx.monitor.service.sys.dto.LoginUser;
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
    private AuthService authService;


    @ApiOperation("用户认证")
    @PostMapping("/login")
    public LoginResult login(@RequestBody LoginUser loginUser) {
        return authService.auth(loginUser);
    }


    @ApiOperation("检查用户认证")
    @PostMapping("/login/check")
    public LoginResult login() {
        return authService.checkAuth();
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
        return R.SUCCESS;
    }

}
