package com.paladin.monitor.service.sys.dto;

import com.styx.common.service.PageParam;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "登录日志查询条件")
public class SysLoggerLoginQuery extends PageParam {

}