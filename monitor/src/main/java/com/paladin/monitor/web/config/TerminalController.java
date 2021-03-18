package com.paladin.monitor.web.config;

import com.paladin.monitor.core.log.OperationLog;
import com.paladin.monitor.core.security.NeedPermission;
import com.paladin.monitor.model.config.ConfigTerminal;
import com.paladin.monitor.service.config.AlarmService;
import com.paladin.monitor.service.config.TerminalService;
import com.paladin.monitor.service.config.VariableService;
import com.paladin.monitor.service.config.dto.StationDeviceDTO;
import com.styx.common.api.R;
import com.styx.common.spring.web.ControllerSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "终端管理")
@RestController
@RequestMapping("/monitor/station/device")
public class TerminalController extends ControllerSupport {

    @Autowired
    private TerminalService stationDeviceService;

    @Autowired
    private VariableService variableService;

    @Autowired
    private AlarmService alarmService;


    @ApiOperation(value = "查询终端详情")
    @GetMapping("/get")
    public ConfigTerminal get(@RequestParam Integer id) {
        return stationDeviceService.get(id);
    }


    @ApiOperation(value = "新增终端信息")
    @PostMapping("/save")
    @NeedPermission("config:station:terminal")
    @OperationLog(model = "终端管理", operate = "新增终端")
    public R save(@RequestBody @Valid StationDeviceDTO stationDeviceDTO, BindingResult bindingResult) {
        validErrorHandler(bindingResult);
        stationDeviceService.saveDevice(stationDeviceDTO);
        return R.success();
    }


    @ApiOperation(value = "修改终端信息")
    @PostMapping("/update")
    @NeedPermission("config:station:editTerminal")
    @OperationLog(model = "终端管理", operate = "更新终端")
    public R update(@RequestBody @Valid StationDeviceDTO stationDeviceDTO, BindingResult bindingResult) {
        validErrorHandler(bindingResult);
        stationDeviceService.updateStationDevice(stationDeviceDTO);
        return R.success();
    }


    @ApiOperation(value = "报警设置")
    @PostMapping("/updateAlarmIds")
    @NeedPermission("config:station:alarmSeting")
    @OperationLog(model = "终端管理", operate = "终端报警设置")
    public R updateAlarmIds(@RequestBody @Valid StationDeviceDTO stationDeviceDTO, BindingResult bindingResult) {
        validErrorHandler(bindingResult);
        stationDeviceService.updateAlarmIds(stationDeviceDTO);
        return R.success();
    }


    @ApiOperation(value = "终端信息删除")
    @PostMapping("/delete")
    @NeedPermission("config:station:deleteTerminal")
    @OperationLog(model = "终端管理", operate = "删除终端")
    public R delete(@RequestParam Integer id) {
        stationDeviceService.removeDevice(id);
        return R.success();
    }


}
