package com.paladin.monitor.web.sys;


import com.paladin.framework.common.R;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.core.Dictionary;
import com.paladin.monitor.core.log.OperationLog;
import com.paladin.monitor.service.sys.SysConstantService;
import com.paladin.monitor.service.sys.dto.SysConstantDTO;
import com.paladin.monitor.service.sys.dto.SysConstantQuery;
import com.paladin.monitor.service.sys.vo.SysConstantVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author cxt
 * @date 2020/9/29
 */
@Api(tags = "数据字典")
@RestController
@RequestMapping("/monitor/sys/constant")
public class SysConstantController extends ControllerSupport {

    @Autowired
    private SysConstantService sysConstantService;


    @ApiOperation("数据字典列表查询")
    @PostMapping("/find/page")
    public PageResult<SysConstantVO> findPage(@RequestBody SysConstantQuery query) {
        return sysConstantService.searchPage(query, SysConstantVO.class);
    }

    @ApiOperation("数据字典详情")
    @GetMapping("/get")
    public SysConstantVO getDetail(@RequestParam String type, @RequestParam String code) {
        return beanCopy(sysConstantService.queryEntityByTypeAndCode(type, code), new SysConstantVO());
    }

    @ApiOperation("添加数据字典")
    @PostMapping("/save")
    @OperationLog(model = "数据字典", operate = "新增常量")
    public R save(@Valid @RequestBody SysConstantDTO sysConstantDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }

        sysConstantService.saveSysConstant(sysConstantDTO);
        return R.success();
    }

    @ApiOperation("更新数据字典")
    @PostMapping("/update")
    @OperationLog(model = "数据字典", operate = "修改常量")
    public R update(@Valid @RequestBody SysConstantDTO sysConstantDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        sysConstantService.updateSysConstant(sysConstantDTO);
        return R.success();
    }

    @ApiOperation("删除数据字典")
    @PostMapping("/delete")
    @OperationLog(model = "数据字典", operate = "删除常量")
    public R delete(@RequestParam String type, @RequestParam String code) {
        sysConstantService.removeSysConstant(type, code);
        return R.success();
    }

    @ApiOperation("按type（单个）查询数据字典")
    @GetMapping("/findByType")
    @ResponseBody
    public List<Dictionary.Constant> findByType(@RequestParam("type") String type) {
        return Dictionary.getConstant(type);
    }

    @ApiOperation(value = "按type（多个）查询数据字典")
    @GetMapping("/findByTypeArr")
    @ResponseBody
    public Map<String, List<Dictionary.Constant>> enumConstants(@RequestParam("type") String[] type) {
        return Dictionary.getConstants(type);
    }

}
