package com.paladin.monitor.web.variable;

import com.paladin.framework.common.R;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.core.config.CVariableContainer;
import com.paladin.monitor.service.variable.ModuleConfigService;
import com.paladin.monitor.service.variable.dto.ModuleConfigDTO;
import com.paladin.monitor.service.variable.vo.ModuleConfigVO;
import com.paladin.monitor.service.variable.vo.VariableVO;
import com.paladin.monitor.web.variable.vo.ModuleConfigDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "模组配置管理")
@RestController
@RequestMapping("/monitor/module/config")
public class ModuleConfigController extends ControllerSupport {

    @Autowired
    private ModuleConfigService moduleConfigService;

    @Autowired
    private CVariableContainer variableContainer;

    @ApiOperation(value = "获取终端模组详细配置")
    @GetMapping("/detail/terminal")
    public ModuleConfigDetail getTerminalConfigDetail(@RequestParam int terminalId) {
        List<ModuleConfigVO> config = moduleConfigService.findModuleConfigOfTerminal(terminalId);
        List<VariableVO> variables = variableContainer.getEnabledVariables();
        return new ModuleConfigDetail(config, variables);
    }

    @ApiOperation(value = "获取终端模组配置列表")
    @GetMapping("/find/terminal")
    public List<ModuleConfigVO> findTerminalConfigList(@RequestParam int terminalId) {
        return moduleConfigService.findModuleConfigOfTerminal(terminalId);
    }

    @ApiOperation(value = "获取终端模组配置")
    @GetMapping("/get")
    public ModuleConfigVO get(@RequestParam int terminalId, @RequestParam int moduleNo) {
        return moduleConfigService.getModuleConfig(terminalId, moduleNo);
    }

    @ApiOperation(value = "修改终端模组配置")
    @PostMapping("/update")
    public R update(@RequestBody @Valid ModuleConfigDTO moduleConfig, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        moduleConfigService.updateModuleConfig(moduleConfig);
        return R.success();
    }


}
