package com.paladin.monitor.web.variable;

import com.paladin.framework.common.R;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.core.log.OperationLog;
import com.paladin.monitor.core.security.NeedPermission;
import com.paladin.monitor.service.variable.VariableService;
import com.paladin.monitor.service.variable.dto.VariableDTO;
import com.paladin.monitor.service.variable.dto.VariableQuery;
import com.paladin.monitor.service.variable.vo.VariableVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "变量管理")
@RestController
@RequestMapping("/monitor/variable")
public class VariableController extends ControllerSupport {

    @Autowired
    private VariableService variableService;


    @ApiOperation(value = "分页获取变量列表")
    @PostMapping("/find/page")
    public PageResult<VariableVO> findList(@RequestBody VariableQuery query) {
        return variableService.searchPage(query, VariableVO.class);
    }

    @ApiOperation(value = "获取展示的变量")
    @GetMapping("/find/showed")
    public List<VariableVO> findShowedList() {
        return variableService.getShowedVariables();
    }

    
    @ApiOperation(value = "获取某终端展示的变量")
    @GetMapping("/find/showed/terminal")
    public List<VariableVO> findShowedListOfTerminal(@RequestParam int terminalId) {
        return variableService.getShowedVariablesOfTerminal(terminalId);
    }


    @ApiOperation(value = "查询变量详情")
    @GetMapping("/get")
    public VariableVO get(@RequestParam Integer id) {
        return variableService.get(id, VariableVO.class);
    }


    @ApiOperation(value = "新增变量信息")
    @PostMapping("/save")
    @NeedPermission("config:variable:add")
    @OperationLog(model = "变量管理", operate = "新增变量")
    public R save(@RequestBody @Valid VariableDTO variableDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        variableService.saveVariable(variableDTO);
        return R.success();
    }

    /**
     * 修改变量信息
     */
    @ApiOperation(value = "修改变量信息")
    @PostMapping("/update")
    @NeedPermission("config:variable:edit")
    @OperationLog(model = "变量管理", operate = "修改变量")
    public R update(@RequestBody @Valid VariableDTO variableDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }

        variableService.updateVariable(variableDTO);
        return R.success();
    }

    /**
     * 变量信息删除
     */
    @ApiOperation(value = "变量信息删除")
    @PostMapping("/delete")
    @NeedPermission("config:variable:delete")
    @OperationLog(model = "变量管理", operate = "删除变量")
    public R delete(@RequestParam Integer id) {
        variableService.removeVariable(id);
        return R.success();
    }


    /**
     * 启用
     */
    @ApiOperation(value = "变量信息启用")
    @PostMapping("/start")
    @NeedPermission("config:variable:stop")
    @OperationLog(model = "变量管理", operate = "启用变量")
    public R start(@RequestParam Integer id) {
        variableService.startVariable(id);
        return R.success();
    }

    /**
     * 停用
     */
    @ApiOperation(value = "变量信息停用")
    @PostMapping("/stop")
    @NeedPermission("config:variable:stop")
    @OperationLog(model = "变量管理", operate = "停用变量")
    public R stop(@RequestParam Integer id) {
        variableService.stopVariable(id);
        return R.success();
    }

}
