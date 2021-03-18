package com.paladin.monitor.core.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2020/10/29
 */
@Getter
@Setter
public class CTerminal {

    private int id;
    private String name;
    private String uid;
    private int stationId;
    private String stationName;
    private int type;
    private String varIds;
    private String noAlarmIds;
    private boolean enabled;

    // 方便搜索
    @JsonIgnore
    private String nodeCode;
    @JsonIgnore
    private int provinceCode;
    @JsonIgnore
    private int cityCode;
    @JsonIgnore
    private int districtCode;

}
