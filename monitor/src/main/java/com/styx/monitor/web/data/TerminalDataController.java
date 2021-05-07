package com.styx.monitor.web.data;

import com.styx.monitor.service.config.TerminalService;
import com.styx.monitor.service.config.vo.StationTerminalVO;
import com.styx.monitor.service.config.vo.TerminalDetailVO;
import com.styx.monitor.service.data.TerminalDataService;
import com.styx.monitor.service.data.vo.TerminalRealData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "终端数据接口")
@RestController
@RequestMapping("/monitor/data")
public class TerminalDataController {

    @Autowired
    private TerminalDataService terminalDataService;

    @Autowired
    private TerminalService terminalService;

    @ApiOperation("终端列表（某节点）")
    @PostMapping("/find/terminal/node")
    public List<StationTerminalVO> findStationTerminalByNode(@RequestParam String node) {
        return terminalService.getEnabledStationTerminalByNode(node);
    }

    @ApiOperation("终端相信信息")
    @PostMapping("/get/terminal/detail")
    public TerminalDetailVO getTerminalDetail(@RequestParam int terminalId) {
        return terminalService.getTerminalDetail(terminalId);
    }

    @ApiOperation(value = "获取终端详细实时数据")
    @GetMapping("/get/real/detail")
    public TerminalRealData getTerminalDataRealtime(@RequestParam int terminalId) {
        return terminalDataService.getTerminalDataRealtime(terminalId);
    }


}
