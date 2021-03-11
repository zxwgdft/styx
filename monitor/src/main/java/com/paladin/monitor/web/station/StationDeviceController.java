package com.paladin.monitor.web.station;

import com.paladin.framework.common.R;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.core.log.OperationLog;
import com.paladin.monitor.core.security.NeedPermission;
import com.paladin.monitor.model.config.ConfigAlarm;
import com.paladin.monitor.model.config.ConfigVariable;
import com.paladin.monitor.model.variable.VariableTemplate;
import com.paladin.monitor.service.config.AlarmService;
import com.paladin.monitor.service.config.TerminalService;
import com.paladin.monitor.service.config.dto.StationDeviceDTO;
import com.paladin.monitor.service.config.dto.StationDeviceQuery;
import com.paladin.monitor.service.config.vo.Station2Device;
import com.paladin.monitor.service.config.vo.StationDeviceVO;
import com.paladin.monitor.service.variable.VariableService;
import com.paladin.monitor.service.variable.VariableTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "终端管理")
@RestController
@RequestMapping("/monitor/station/device")
public class StationDeviceController extends ControllerSupport {

    @Autowired
    private TerminalService stationDeviceService;

    @Autowired
    private VariableService variableService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private VariableTemplateService variableTemplateService;


    @ApiOperation(value = "查找站点终端简要信息")
    @PostMapping("/find/all/simple")
    public List<Station2Device> findDevicesSimple(@RequestBody StationDeviceQuery query) {
        return stationDeviceService.findSimpleStationDevices(query);
    }

    @ApiOperation(value = "分页获取终端管理列表")
    @PostMapping("/find/page")
    public Map<String, Object> findList(@RequestBody StationDeviceQuery query) {
        Map<String, Object> map = new HashMap<String, Object>();
        //1.分页获取终端管理列表
        map.put("PageResult", stationDeviceService.searchPage(query, StationDeviceVO.class));
        //2.查询报警列表
        List<ConfigAlarm> alarmList = alarmService.findAll();
        //3.查询参数列表
        List<ConfigVariable> variableList = variableService.findAll();
        //4.查询模板列表
        List<VariableTemplate> variableTemplateList = variableTemplateService.findAll();

        map.put("alarmList", alarmList);
        map.put("variableList", variableList);
        map.put("variableTemplateList", variableTemplateList);
        return map;
    }


    @ApiOperation(value = "查询终端详情")
    @GetMapping("/get")
    public StationDeviceVO get(@RequestParam Integer id) {
        return stationDeviceService.get(id, StationDeviceVO.class);
    }


    @ApiOperation(value = "新增终端信息")
    @PostMapping("/save")
    @NeedPermission("config:station:terminal")
    @OperationLog(model = "终端管理", operate = "新增终端")
    public R save(@RequestBody @Valid StationDeviceDTO stationDeviceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        stationDeviceService.saveDevice(stationDeviceDTO);
        return R.success();
    }


    @ApiOperation(value = "修改终端信息")
    @PostMapping("/update")
    @NeedPermission("config:station:editTerminal")
    @OperationLog(model = "终端管理", operate = "更新终端")
    public R update(@RequestBody @Valid StationDeviceDTO stationDeviceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        stationDeviceService.updateStationDevice(stationDeviceDTO);
        return R.success();
    }


    @ApiOperation(value = "报警设置")
    @PostMapping("/updateAlarmIds")
    @NeedPermission("config:station:alarmSeting")
    @OperationLog(model = "终端管理", operate = "终端报警设置")
    public R updateAlarmIds(@RequestBody @Valid StationDeviceDTO stationDeviceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
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
