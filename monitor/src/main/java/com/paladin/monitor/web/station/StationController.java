package com.paladin.monitor.web.station;

import com.paladin.framework.common.R;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.log.OperationLog;
import com.paladin.monitor.core.security.NeedPermission;
import com.paladin.monitor.service.config.StationService;
import com.paladin.monitor.service.config.dto.StationDTO;
import com.paladin.monitor.service.config.dto.StationEditDTO;
import com.paladin.monitor.service.config.dto.StationPageQuery;
import com.paladin.monitor.service.config.dto.StationQuery;
import com.paladin.monitor.service.config.vo.SimpleStationVO;
import com.paladin.monitor.service.config.vo.StationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "站点管理")
@RestController
@RequestMapping("/monitor/station")
public class StationController extends ControllerSupport {

    @Autowired
    private StationService stationService;


    @ApiOperation(value = "分页获取站点管理列表")
    @PostMapping("/find/page")
    public PageResult<StationVO> findPage(@RequestBody StationPageQuery query) {
        return stationService.findStationPage(query);
    }

    @ApiOperation(value = "获取所有站点简单列表")
    @PostMapping("/find/all")
    public List<SimpleStationVO> findSimpleList(@RequestBody StationQuery query) {
        return stationService.findSimpleStations(query);
    }

    @ApiOperation(value = "获取站点地区列表")
    @GetMapping("/district")
    public List<CTerminalContainer.SimpleDistrict> getEffectDistricts() {
        return stationService.getEffectDistricts();
    }

    @ApiOperation(value = "查询站点详情")
    @GetMapping("/get")
    public StationVO get(@RequestParam Integer id) {
        return stationService.get(id, StationVO.class);
    }

    @ApiOperation(value = "新增站点信息")
    @PostMapping("/save")
    @NeedPermission("config:station:add")
    @OperationLog(model = "站点管理", operate = "新增站点")
    public R save(@RequestBody @Valid StationDTO stationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        stationService.saveStation(stationDTO);
        return R.success();
    }

    @ApiOperation(value = "新增测试站点信息")
    @PostMapping("/save/test")
    @NeedPermission("config:station:addTest")
    @OperationLog(model = "站点管理", operate = "新增测试站点")
    public R saveTestStation(@RequestBody @Valid StationDTO stationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        stationService.saveTestStation(stationDTO);
        return R.success();
    }

    @ApiOperation(value = "修改站点信息")
    @PostMapping("/update")
    @NeedPermission("config:station:edit")
    @OperationLog(model = "站点管理", operate = "更新站点")
    public R update(@RequestBody @Valid StationEditDTO stationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        stationService.updateStation(stationDTO);
        return R.success();
    }

    @ApiOperation(value = "站点启用")
    @GetMapping("/enabled")
    @NeedPermission("config:station:edit")
    @OperationLog(model = "站点管理", operate = "站点启用")
    public R enabled(@RequestParam Integer id) {
        stationService.updateStationEnabled(id, true);
        return R.success();
    }

    @ApiOperation(value = "站点停用")
    @GetMapping("/disabled")
    @NeedPermission("config:station:edit")
    @OperationLog(model = "站点管理", operate = "站点停用")
    public R disabled(@RequestParam Integer id) {
        stationService.updateStationEnabled(id, false);
        return R.success();
    }

    @ApiOperation(value = "站点设置排序号")
    @GetMapping("/set/order")
    public R updateOrderNo(@RequestParam Integer id, @RequestParam Integer orderNo) {
        stationService.uploadStationOrderNo(id, orderNo);
        return R.success();
    }

    @ApiOperation(value = "站点信息删除")
    @PostMapping("/delete")
    @NeedPermission("config:station:delete")
    @OperationLog(model = "站点管理", operate = "删除站点")
    public R delete(@RequestParam Integer id) {
        stationService.removeStation(id);
        return R.success();
    }


    @ApiOperation("站点重置服务节点")
    @GetMapping("/set/node")
    @NeedPermission("config:station:reset")
    @OperationLog(model = "站点管理", operate = "站点重制节点")
    public R moveToNode(@RequestParam int stationId, @RequestParam String targetNode) {
        stationService.changeStationServerNode(stationId, targetNode);
        return R.success();
    }


    @ApiOperation("测试终端转为正式终端")
    @GetMapping("/turn/formal")
    @OperationLog(model = "站点管理", operate = "测试转正式站点")
    public R turnFormal(@RequestParam int stationId) {
        stationService.turnFormalStation(stationId);
        return R.success();
    }
}


















