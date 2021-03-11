package com.paladin.monitor.web.station;

import com.paladin.framework.common.R;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.model.config.VideoManagement;
import com.paladin.monitor.service.config.VideoManagementService;
import com.paladin.monitor.service.config.dto.VideoManagementDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: cxt
 * @time: 2021/1/22
 */
@Api(tags = "视频管理")
@RestController
@RequestMapping("/monitor/video/management")
public class VideoManagementController extends ControllerSupport {

    private VideoManagementService videoManagementService;

    /**
     * 根据终端id查询地址
     */
    @GetMapping("/get")
    @ApiOperation("根据终端id查询地址")
    public List<VideoManagement> getAddress(@RequestParam("stationDeviceId") Integer stationDeviceId) {
        return this.videoManagementService.getAddress(stationDeviceId);
    }

    /**
     * 修改地址
     */
    @ApiOperation("修改地址")
    @PostMapping("/update")
    public R updateAddress(@RequestBody VideoManagementDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        this.videoManagementService.updateAddress(dto);
        return R.success();
    }

    /**
     * 删除
     */
    @GetMapping("/delete")
    @ApiOperation("删除地址")
    public R remove(@RequestParam("id") Integer id) {
        this.videoManagementService.remove(id);
        return R.success();
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    @ApiOperation("新增地址")
    public R add(@RequestBody VideoManagementDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        this.videoManagementService.add(dto);
        return R.success();
    }

    @Autowired
    public void setVideoManagementService(VideoManagementService videoManagementService) {
        this.videoManagementService = videoManagementService;
    }
}
