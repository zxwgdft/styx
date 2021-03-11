package com.paladin.monitor.service.data.vo;

import com.paladin.monitor.core.config.CTerminal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/1/11
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "用于大屏展示地图数据")
public class DataTerminal4Show {

    @ApiModelProperty("终端ID")
    private int terminalId;
    @ApiModelProperty("终端名称")
    private String terminalName;
    @ApiModelProperty("站点名称")
    private String stationName;
    @ApiModelProperty("是否在线")
    private boolean isOnline;
    @ApiModelProperty("是否故障")
    private boolean isAlarming;
    @ApiModelProperty("经度")
    private String longitude;
    @ApiModelProperty("纬度")
    private String latitude;
    @ApiModelProperty("所在区县代码")
    private int districtCode;

    public DataTerminal4Show() {

    }

    public DataTerminal4Show(CTerminal terminal) {
        this.terminalId = terminal.getId();
        this.terminalName = terminal.getName();
        this.stationName = terminal.getStationName();
        this.latitude = terminal.getLatitude();
        this.longitude = terminal.getLongitude();
        this.districtCode = terminal.getDistrictCode();
    }


}
