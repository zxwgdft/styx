package com.paladin.monitor.web.internal;


import com.paladin.monitor.service.config.VersionConfigService;
import com.paladin.monitor.service.config.dto.VersionConfig;
import com.paladin.monitor.service.config.dto.VersionUpdate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TontoZhou
 * @since 2020/10/30
 */
@Api(tags = "内部参数配置接口")
@RestController
@RequestMapping("/internal/")
public class InternalController {

    @Autowired
    private VersionConfigService versionConfigService;

    @ApiOperation("配置版本更新")
    @PostMapping("/config/update")
    public VersionConfig getVariable(@RequestBody VersionUpdate versionUpdate) {
        return versionConfigService.getVersionConfig(versionUpdate);
    }


}
