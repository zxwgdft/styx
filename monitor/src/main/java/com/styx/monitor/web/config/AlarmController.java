package com.styx.monitor.web.config;

import com.styx.common.service.PageResult;
import com.styx.monitor.model.config.ConfigAlarm;
import com.styx.monitor.service.config.AlarmService;
import com.styx.monitor.service.config.dto.AlarmQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TontoZhou
 * @since 2021/4/8
 */
@Api(tags = "报警")
@RestController
@RequestMapping("/monitor/config/alarm")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;


    @ApiOperation("报警列表(分页)")
    @PostMapping("/find/page")
    public PageResult<ConfigAlarm> findPage(@RequestBody AlarmQuery query) {
        return alarmService.findPage(query, query);
    }

}
