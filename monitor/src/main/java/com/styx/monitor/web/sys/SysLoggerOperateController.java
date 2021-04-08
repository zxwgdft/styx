package com.styx.monitor.web.sys;

import com.styx.monitor.model.sys.SysLoggerOperate;
import com.styx.monitor.service.sys.SysLoggerOperateService;
import com.styx.monitor.service.sys.dto.SysLoggerOperateQuery;
import com.styx.common.service.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "操作日志")
@RestController
@RequestMapping("/monitor/sys/logger/operate")
public class SysLoggerOperateController {

    @Autowired
    private SysLoggerOperateService sysLoggerOperateService;

    @ApiOperation("日志列表")
    @PostMapping("/find/page")
    public PageResult<SysLoggerOperate> findPage(@RequestBody SysLoggerOperateQuery query) {
        return sysLoggerOperateService.findPage(query);
    }
}