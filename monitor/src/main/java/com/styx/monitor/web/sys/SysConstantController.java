package com.styx.monitor.web.sys;


import com.styx.monitor.service.sys.SysConstantService;
import com.styx.monitor.web.sys.vo.ConstantJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "字典常量")
@RestController
@RequestMapping("/monitor/sys/constant")
public class SysConstantController {

    @Autowired
    private SysConstantService constantService;

    @ApiOperation("获取所有常量")
    @PostMapping("/get/all")
    public ConstantJson getConstantJson(@RequestParam(required = false) Long version) {
        SysConstantService.ConstantContainer constantContainer = constantService.getConstantContainer();
        if (version == null || constantContainer.getVersion() != version) {
            return new ConstantJson(constantContainer.getVersion(), constantContainer.getConstantJson());
        } else {
            // 版本一致情况下我们不需要返回json
            return new ConstantJson(version, null);
        }
    }

}