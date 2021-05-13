package com.styx.support.web;

import com.styx.support.service.WeiXinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TontoZhou
 * @since 2021/1/12
 */
@Api(tags = "微信接口")
@RestController
@RequestMapping("/support/wx")
public class WeiXinController {

    @Autowired
    private WeiXinService weiXinService;

    @ApiOperation(value = "获取微信openid")
    @GetMapping("/get/openid")
    public String getOpenId(@RequestParam String jsCode) {
        return weiXinService.getOpenIdOfWX(jsCode);
    }

}
