package com.styx.monitor.web.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.styx.monitor.model.config.ConfigTerminal;
import com.styx.monitor.service.config.TerminalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/4/8
 */
@Api(tags = "终端")
@RestController
@RequestMapping("/monitor/config/terminal")
public class TerminalController {

    @Autowired
    private TerminalService terminalService;

    @ApiOperation("终端列表（某站点）")
    @PostMapping("/find/station")
    public List<ConfigTerminal> findByStation(@RequestParam Integer id) {
        return terminalService.findList(new LambdaQueryWrapper<ConfigTerminal>().eq(ConfigTerminal::getStationId, id));
    }

}
