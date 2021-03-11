package com.paladin.monitor.service.data.dto;

import com.paladin.framework.service.PageResult;
import com.paladin.monitor.service.config.vo.StationDeviceMonitorVO;
import com.paladin.monitor.service.variable.vo.VariableVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/12/2
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "终端&展示变量&实时数据")
public class TerminalListRealtime {

    @ApiModelProperty("终端信息")
    private PageResult<StationDeviceMonitorVO> devices;

    @ApiModelProperty("展示变量")
    private List<VariableVO> variables;

    @ApiModelProperty("实时数据")
    private List<TerminalRealtime> data;

}
