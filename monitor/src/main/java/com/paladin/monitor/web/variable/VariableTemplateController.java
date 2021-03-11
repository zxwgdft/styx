package com.paladin.monitor.web.variable;

import com.paladin.framework.common.R;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.model.config.ConfigVariable;
import com.paladin.monitor.model.variable.VariableTemplate;
import com.paladin.monitor.service.variable.VariableService;
import com.paladin.monitor.service.variable.VariableTemplateService;
import com.paladin.monitor.service.variable.dto.VariableTemplateDTO;
import com.paladin.monitor.service.variable.dto.VariableTemplateQuery;
import com.paladin.monitor.service.variable.vo.VariableTemplateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "模板管理")
@RestController
@RequestMapping("/monitor/variable/template")
public class VariableTemplateController extends ControllerSupport {

    @Autowired
    private VariableTemplateService variableTemplateService;//模板

    @Autowired
    private VariableService variableService;


    /**
     * 查询模板列表
     *
     * @param query
     * @return
     */
    @ApiOperation(value = "分页获取变量模板列表")
    @PostMapping("/find/page")
    public PageResult<VariableTemplateVO> findList(@RequestBody VariableTemplateQuery query) {

        return variableTemplateService.searchPage(query, VariableTemplateVO.class);
    }


    //跳转新增或修改页面时查询所有变量信息
    @ApiOperation(value = "跳转新增或修改页面时查询所有变量信息")
    @GetMapping("/findVariableList")
    public List<ConfigVariable> findList() {

        return variableService.findAll();
    }


    @ApiOperation(value = "新增模板")
    @PostMapping("/save")
    public Object save(@Valid @RequestBody VariableTemplateDTO save, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        VariableTemplate model = beanCopy(save, new VariableTemplate());
        return R.status(variableTemplateService.save(model), "保存失败");
    }

    @ApiOperation(value = "修改模板")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody VariableTemplateDTO update, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }

        return R.status(variableTemplateService.updateTemplate(update), "保存失败");
    }


    @ApiOperation(value = "模板详情")
    @GetMapping("/get")
    public VariableTemplateVO get(@RequestParam Integer id) {

        return beanCopy(variableTemplateService.get(id), new VariableTemplateVO());
    }

    /**
     * 查询详情时，先查询模板中的变量
     *
     * @param
     * @return
     */
//    @ApiOperation(value = "询模板中的变量")
//    @PostMapping(value = "/find/checked/data")
//    public List<DeviceVariableTemplateVO> findCheckedData(@RequestBody DeviceVariableQuery query) {
//
//        return deviceTemplateService.findEnabledAndCheckedPageData(query);
//    }
    @ApiOperation(value = "删除模板")
    @PostMapping(value = "/delete")
    public R delete(@RequestParam Integer id) {
        return R.status(variableTemplateService.removeByPrimaryKey(id), "删除模板失败");
    }


}
