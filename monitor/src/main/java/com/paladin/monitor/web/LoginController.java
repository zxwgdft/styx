package com.paladin.monitor.web;

import com.paladin.framework.common.R;
import com.paladin.framework.exception.BusinessException;
import com.paladin.monitor.core.MonitorUserSession;
import com.paladin.monitor.core.security.Menu;
import com.paladin.monitor.service.sys.AuthService;
import com.paladin.monitor.service.sys.SysUserService;
import com.paladin.monitor.service.sys.VerifyCodeService;
import com.paladin.monitor.service.sys.dto.LoginSuccess;
import com.paladin.monitor.service.sys.dto.LoginUser;
import com.paladin.monitor.service.sys.dto.RedirectLoginUser;
import com.paladin.monitor.util.verifyCode.VerifyCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/monitor")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private AuthService authService;

    @Autowired
    private VerifyCodeService verifyCodeService;

    @ApiOperation(value = "生成验证码")
    @GetMapping("/verifyCode")
    public void verifyCode(HttpServletRequest request, HttpServletResponse response) {
        try {
            //设置长宽
            VerifyCode verifyCode = verifyCodeService.createVerifyCode();
            //设置响应头
            response.setHeader("Pragma", "no-cache");
            //设置响应头
            response.setHeader("Cache-Control", "no-cache");
            //在代理服务器端防止缓冲
            response.setDateHeader("Expires", 0);
            //设置响应内容类型
            response.setContentType("image/jpeg");
            response.getOutputStream().write(verifyCode.getImgBytes());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new BusinessException("生成验证码异常", e);
        }
    }

    @ApiOperation("用户认证")
    @PostMapping("/login")
    public LoginSuccess login(@RequestBody LoginUser loginUser) {
        return authService.auth(loginUser);
    }

    @ApiOperation("用户认证")
    @PostMapping("/login/token")
    public LoginSuccess login() {
        return authService.auth();
    }


    @ApiOperation("用户认证(微信小程序登录)")
    @PostMapping("/login/wx")
    public LoginSuccess login(@RequestParam String jsCode) {
        return authService.authWX(jsCode);
    }


    @ApiOperation("微信绑定")
    @PostMapping("/wx/bind")
    public R bindWeiXin(@RequestParam String jsCode) {
        authService.bindWeiXin(jsCode);
        return R.success();
    }

    @ApiOperation("微信解绑")
    @PostMapping("/wx/unbind")
    public R unbindWeiXin(@RequestParam String jsCode) {
        authService.unbindWeiXin(jsCode);
        return R.success();
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

    @ApiOperation("修改密码")
    @PostMapping("/update/password")
    public R updatePasswordSelf(@RequestParam String newPassword, @RequestParam String oldPassword) {
        sysUserService.updateSelfPassword(newPassword, oldPassword);
        return R.success();
    }

    @ApiOperation("获取菜单")
    @GetMapping("/menus")
    public List<Menu> getMenu() {
        return MonitorUserSession.getCurrentUserSession().getMenuResources();
    }

    @ApiOperation("获取权限编码")
    @GetMapping("/permission/code")
    public Collection<String> getPermissionCode() {
        return MonitorUserSession.getCurrentUserSession().getPermissionCodes();
    }

    @ApiOperation("用户认证并获取重定向地址")
    @PostMapping("/auth/redirect")
    public String authAndRedirect(RedirectLoginUser loginUser, HttpServletRequest request) {
        return authService.authAndGetRedirectUrl(loginUser, request);
    }

    @ApiOperation("用户认证token")
    @GetMapping("/auth/token")
    public LoginSuccess authToken(@RequestParam String token) {
        return authService.getAuthResultByToken(token);
    }


}
