package com.paladin.monitor.web.sys;


import com.paladin.monitor.model.sys.SysLoggerLogin;
import com.paladin.monitor.service.sys.SysLoggerLoginService;
import com.paladin.monitor.service.sys.dto.SysLoggerLoginQuery;
import com.styx.common.service.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "登录日志管理")
@RestController
@RequestMapping("/monitor/sys/logger/login")
public class SysLoggerLoginController {

    @Autowired
    private SysLoggerLoginService sysLoggerLoginService;

    @ApiOperation("登录日志查询")
    @PostMapping("/find/page")
    public PageResult<SysLoggerLogin> findPage(@RequestBody SysLoggerLoginQuery query) {
        return sysLoggerLoginService.findPage(query);
    }


}