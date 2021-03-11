package com.paladin.monitor.web.variable;

import com.paladin.framework.common.R;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.core.config.CVariableContainer;
import com.paladin.monitor.model.variable.ModuleConfigTemplate;
import com.paladin.monitor.service.variable.ModuleConfigTemplateService;
import com.paladin.monitor.service.variable.dto.ModuleConfigTemplateDTO;
import com.paladin.monitor.service.variable.dto.ModuleConfigTemplateQuery;
import com.paladin.monitor.service.variable.vo.VariableVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author: cxt
 * @time: 2021/2/2
 */
@Api(tags = "组态模板管理")
@RestController
@RequestMapping("/monitor/module/config/template")
public class ModuleConfigTemplateController extends ControllerSupport {
    @Autowired
    private ModuleConfigTemplateService moduleConfigTemplateService;

    @Autowired
    private CVariableContainer variableContainer;

    @ApiOperation("查询模板")
    @GetMapping("/get")
    public PageResult<ModuleConfigTemplate> getModule(ModuleConfigTemplateQuery query) {
        return this.moduleConfigTemplateService.searchPage(query, ModuleConfigTemplate.class);
    }

    @ApiOperation("保存组态模板")
    @PostMapping("/save")
    public R saveModule(@RequestBody @Valid ModuleConfigTemplateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        this.moduleConfigTemplateService.saveModule(dto);
        return R.success();
    }

    @ApiOperation("删除组态模板")
    @GetMapping("/remove")
    public R removeModule(@RequestParam Integer id) {
        this.moduleConfigTemplateService.removeModule(id);
        return R.success();
    }

    @ApiOperation("修改组态模板")
    @PostMapping("/update")
    public R updateModule(@RequestBody @Valid ModuleConfigTemplateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        this.moduleConfigTemplateService.updateModel(dto);
        return R.success();
    }

    @ApiOperation("组态模板详情")
    @GetMapping("/detail")
    public ModuleConfigTemplate getDetail(@RequestParam("id") String id) {
        return this.moduleConfigTemplateService.get(id, ModuleConfigTemplate.class);
    }

    @ApiOperation("配置数据")
    @GetMapping("/variables")
    public List<VariableVO> getVariables() {
        return variableContainer.getEnabledVariables();
    }

    @ApiOperation("分组查询")
    @GetMapping("/grouping")
    public Map<Integer, List<ModuleConfigTemplate>> grouping(){
        return this.moduleConfigTemplateService.grouping();
    }

}

