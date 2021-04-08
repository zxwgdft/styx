package com.styx.monitor.web.sys;


import com.styx.monitor.model.sys.SysLoggerLogin;
import com.styx.monitor.service.sys.SysLoggerLoginService;
import com.styx.monitor.service.sys.dto.SysLoggerLoginQuery;
import com.styx.common.service.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "登录日志")
@RestController
@RequestMapping("/monitor/sys/logger/login")
public class SysLoggerLoginController {

    @Autowired
    private SysLoggerLoginService sysLoggerLoginService;

    @ApiOperation("日志列表")
    @PostMapping("/find/page")
    public PageResult<SysLoggerLogin> findPage(@RequestBody SysLoggerLoginQuery query) {
        return sysLoggerLoginService.findPage(query);
    }


}