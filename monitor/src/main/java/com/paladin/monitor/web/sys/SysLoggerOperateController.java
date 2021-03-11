package com.paladin.monitor.web.sys;

import com.paladin.framework.service.PageResult;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.model.sys.SysLoggerOperate;
import com.paladin.monitor.service.sys.SysLoggerOperateService;
import com.paladin.monitor.service.sys.dto.SysLoggerOperateQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "操作日志管理")
@RestController
@RequestMapping("/monitor/sys/logger/operate")
public class SysLoggerOperateController extends ControllerSupport {

    @Autowired
    private SysLoggerOperateService sysLoggerOperateService;

    @ApiOperation("操作日志查询")
    @PostMapping("/find/page")
    public PageResult<SysLoggerOperate> findPage(@RequestBody SysLoggerOperateQuery query) {
        return sysLoggerOperateService.searchPage(query);
    }
}