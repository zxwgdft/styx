package com.styx.monitor.web.config;

import com.styx.common.service.PageResult;
import com.styx.monitor.model.config.ConfigVariable;
import com.styx.monitor.service.config.VariableService;
import com.styx.monitor.service.config.dto.VariableQuery;
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
@Api(tags = "变量")
@RestController
@RequestMapping("/monitor/config/variable")
public class VariableController {

    @Autowired
    private VariableService variableService;


    @ApiOperation("变量列表(分页)")
    @PostMapping("/find/page")
    public PageResult<ConfigVariable> findPage(@RequestBody VariableQuery query) {
        return variableService.findPage(query, query);
    }

}
