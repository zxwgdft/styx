package com.styx.monitor.service.config.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/3/30
 */
@Getter
@Setter
public class StationTerminal {

    private int id;
    private String name;
    private int type;
    private String uid;

    private int stationId;
    private String stationName;

    private String nodeCode;

    private int provinceCode;
    private int cityCode;
    private int districtCode;

}
