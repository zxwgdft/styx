package com.paladin.monitor.web.data;

import com.paladin.framework.exception.BusinessException;
import com.paladin.monitor.service.data.DataAnalysisService;
import com.paladin.monitor.service.data.dto.AnalysisDayQuery;
import com.paladin.monitor.service.data.dto.AnalysisHourQuery;
import com.paladin.monitor.service.data.dto.PackageManagerQuery;
import com.paladin.monitor.service.data.dto.PackageTerminalQuery;
import com.paladin.monitor.service.data.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/12/15
 */
@Api(tags = "终端数据统计接口")
@RestController
@RequestMapping("/monitor/analysis")
public class DataAnalysisController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @ApiOperation(value = "获取市级日报统计数据")
    @PostMapping("/hour/city")
    public List<AnalysisHour4City> analyzeHourDataForCity(@RequestBody AnalysisHourQuery query) {
        return dataAnalysisService.analyzeHourDataForCity(query);
    }

    @ApiOperation(value = "获取区县日报统计数据")
    @PostMapping("/hour/district")
    public List<AnalysisHour4District> analyzeHourDataForDistrict(@RequestBody AnalysisHourQuery query) {
        return dataAnalysisService.analyzeHourDataForDistrict(query);
    }

    @ApiOperation(value = "获取站点日报统计数据")
    @PostMapping("/hour/station")
    public List<AnalysisHour4Station> analyzeHourDataForStation(@RequestBody AnalysisHourQuery query) {
        return dataAnalysisService.analyzeHourDataForStation(query);
    }

    @ApiOperation(value = "获取站点日报统计数据")
    @PostMapping("/hour/terminal")
    public List<AnalysisHour4Terminal> analyzeHourDataForTerminal(@RequestBody AnalysisHourQuery query) {
        return dataAnalysisService.analyzeHourDataForTerminal(query);
    }

    @ApiOperation(value = "获取市级月报统计数据")
    @PostMapping("/day/city")
    public List<AnalysisDay4City> analyzeDayDataForCity(@RequestBody AnalysisDayQuery query) {
        return dataAnalysisService.analyzeDayDataForCity(query);
    }

    @ApiOperation(value = "获取区县月报统计数据")
    @PostMapping("/day/district")
    public List<AnalysisDay4District> analyzeDayDataForDistrict(@RequestBody AnalysisDayQuery query) {
        return dataAnalysisService.analyzeDayDataForDistrict(query);
    }

    @ApiOperation(value = "获取站点月报统计数据")
    @PostMapping("/day/station")
    public List<AnalysisDay4Station> analyzeDayDataForStation(@RequestBody AnalysisDayQuery query) {
        return dataAnalysisService.analyzeDayDataForStation(query);
    }

    @ApiOperation(value = "获取终端月报统计数据")
    @PostMapping("/day/terminal")
    public List<AnalysisDay4Terminal> analyzeDayDataForTerminal(@RequestBody AnalysisDayQuery query) {
        return dataAnalysisService.analyzeDayDataForTerminal(query);
    }

    @ApiOperation(value = "导出站点月报数据")
    @GetMapping("/export/terminal")
    public void exportDayReportForTerminal(@RequestParam int stationId, @RequestParam String date, HttpServletResponse response) {
        try {
            dataAnalysisService.exportDayReportForTerminal(stationId, date, response.getOutputStream());
            response.setHeader("Content-Disposition", "attachment; filename=monthReport.xls");
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
        } catch (IOException e) {
            throw new BusinessException("导出excel文件异常", e);
        }
    }

    @ApiOperation(value = "获取大屏最近各变量统计数据")
    @PostMapping("/day/show")
    public List<DataTrend4Show> analyzeDayDataForShow(@RequestParam(required = false) Integer districtId) {
        return dataAnalysisService.analyzeDayDataForShow(districtId);
    }

    @ApiOperation(value = "获取终端分析数据包")
    @PostMapping("/package/terminal")
    public PackageData4Terminal getPackageData4Terminal(@RequestBody PackageTerminalQuery query) {
        return dataAnalysisService.getPackageData4Terminal(query);
    }

    @ApiOperation(value = "获取管理分析数据包")
    @PostMapping("/package/manager")
    public PackageData4Show getPackageData4Manager(@RequestBody PackageManagerQuery query) {
        return dataAnalysisService.getPackageData4Manager(query);
    }

}
