package com.styx.monitor.web.config;

import com.styx.common.service.PageResult;
import com.styx.monitor.model.config.ConfigStation;
import com.styx.monitor.service.config.StationService;
import com.styx.monitor.service.config.dto.StationQuery;
import com.styx.monitor.service.config.vo.SimpleStation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/4/8
 */
@Api(tags = "站点")
@RestController
@RequestMapping("/monitor/config/station")
public class StationController {

    @Autowired
    private StationService stationService;

    @ApiOperation("站点列表（简单）")
    @PostMapping("/find/all/simple")
    public List<SimpleStation> findSimpleList(@RequestBody StationQuery query) {
        return stationService.findSimpleList(query);
    }

    @ApiOperation("站点列表(分页)")
    @PostMapping("/find/page")
    public PageResult<ConfigStation> findPage(@RequestBody StationQuery query) {
        return stationService.findStationPage(query);
    }

}
