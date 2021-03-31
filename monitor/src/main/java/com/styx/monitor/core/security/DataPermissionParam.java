package com.styx.monitor.core.security;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/5/12
 */
@Getter
@Setter
public class DataPermissionParam {

    // 省编码
    private Integer provinceCode;
    private List<Integer> provinceCodes;

    // 市编码
    private Integer cityCode;
    private List<Integer> cityCodes;

    // 区县编码
    private Integer districtCode;
    private List<Integer> districtCodes;

    // 管理站点
    private Integer stationId;
    private List<Integer> stationIds;

    // 是否有所有权限
    private boolean hasAll;

    // 是否有权限
    private boolean hasPermission = true;

}
