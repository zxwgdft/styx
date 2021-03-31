package com.styx.monitor.service.sys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2019/12/26
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "其他系统跳转登录用户")
public class RedirectLoginUser {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("前往页面ID")
    private String forward;

    @ApiModelProperty("页面布局")
    private String layout;

}
