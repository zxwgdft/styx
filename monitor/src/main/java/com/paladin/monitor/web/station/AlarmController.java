package com.paladin.monitor.web.station;

import com.paladin.framework.common.R;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.core.log.OperationLog;
import com.paladin.monitor.core.security.NeedPermission;
import com.paladin.monitor.service.config.AlarmService;
import com.paladin.monitor.service.config.dto.AlarmDTO;
import com.paladin.monitor.service.config.dto.AlarmQuery;
import com.paladin.monitor.service.config.vo.AlarmVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "报警设置")
@RestController
@RequestMapping("/monitor/station/alarm")
public class AlarmController extends ControllerSupport {

    @Autowired
    private AlarmService alarmService;


    @ApiOperation(value = "分页获取报警设置列表")
    @PostMapping("/find/page")
    public PageResult<AlarmVO> findList(@RequestBody AlarmQuery query) {
        return alarmService.searchPage(query, AlarmVO.class);
    }

    @ApiOperation(value = "报警设置详情")
    @GetMapping("/get")
    public AlarmVO get(@RequestParam Integer id) {
        return alarmService.get(id, AlarmVO.class);
    }

    @ApiOperation(value = "新增报警设置信息")
    @PostMapping("/save")
    @NeedPermission("config:alarm:add")
    @OperationLog(model = "报警管理", operate = "新增报警")
    public R save(@RequestBody @Valid AlarmDTO alarmDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        alarmService.saveAlarm(alarmDTO);
        return R.success();
    }

    @ApiOperation(value = "修改报警设置信息")
    @PostMapping("/update")
    @NeedPermission("config:alarm:edit")
    @OperationLog(model = "报警管理", operate = "修改报警")
    public R update(@RequestBody @Valid AlarmDTO alarmDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        alarmService.updateAlarm(alarmDTO);
        return R.success();
    }

    @ApiOperation(value = "报警设置删除")
    @PostMapping("/delete")
    @NeedPermission("config:alarm:delete")
    @OperationLog(model = "报警管理", operate = "删除报警")
    public R delete(@RequestParam Integer id) {
        alarmService.removeAlarm(id);
        return R.success();
    }

    @ApiOperation(value = "报警信息启用")
    @PostMapping("/start")
    @NeedPermission("config:alarm:stop")
    @OperationLog(model = "报警管理", operate = "启用报警")
    public R start(@RequestParam Integer id) {
        alarmService.startAlarm(id);
        return R.success();
    }

    @ApiOperation(value = "报警信息停用")
    @PostMapping("/stop")
    @NeedPermission("config:alarm:stop")
    @OperationLog(model = "报警管理", operate = "停用报警")
    public R stop(@RequestParam Integer id) {
        alarmService.stopAlarm(id);
        return R.success();
    }

}
