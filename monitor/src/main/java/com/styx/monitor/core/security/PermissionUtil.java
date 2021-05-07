package com.styx.monitor.core.security;


import com.styx.monitor.core.MonitorUserSession;
import com.styx.monitor.core.distrcit.District;
import com.styx.monitor.core.distrcit.DistrictUtil;
import com.styx.monitor.model.org.OrgUser;
import com.styx.common.cache.DataCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限工具类（包括数据权限处理、功能权限判断、权限获取）
 */
@Component
public class PermissionUtil {

    @Autowired
    private DataCacheManager source;

    @PostConstruct
    public void init() {
        // 静态化
        PermissionUtil.dataCacheManager = this.source;
    }


    private static DataCacheManager dataCacheManager;

    public static DataPermissionParam getUserDataPermission() {
        MonitorUserSession userSession = MonitorUserSession.getCurrentUserSession();
        DataPermissionParam param = new DataPermissionParam();

        if (!userSession.isSystemAdmin()) {
            int userType = userSession.getUserType();

            if (userType == OrgUser.USER_TYPE_APP_ADMIN) {
                param.setHasAll(true);
            } else if (userType == OrgUser.USER_TYPE_PERSONNEL) {
                param.setHasPermission(false);
            } else if (userType == OrgUser.USER_TYPE_STATION) {
                Integer[] stations = userSession.getStations();
                if (stations != null && stations.length > 0) {
                    if (stations.length == 1) {
                        param.setStationId(stations[0]);
                    } else {
                        param.setStationIds(Arrays.asList(stations));
                    }
                } else {
                    param.setHasPermission(false);
                }
            } else if (userType == OrgUser.USER_TYPE_DISTRICT) {
                Integer[] districtCodes = userSession.getDistricts();
                if (districtCodes != null && districtCodes.length > 0) {
                    if (districtCodes.length == 1) {
                        int code = districtCodes[0];

                        if (code == DistrictUtil.CHINA_CODE) {
                            param.setHasAll(true);
                            return param;
                        }

                        District district = DistrictUtil.getDistrict(code);
                        if (district != null) {
                            int lvl = district.getLevel();
                            if (lvl == 1) {
                                param.setProvinceCode(code);
                            } else if (lvl == 2) {
                                param.setCityCode(code);
                            } else if (lvl == 3) {
                                param.setDistrictCode(code);
                            } else {
                                param.setHasPermission(false);
                            }
                        } else {
                            param.setHasPermission(false);
                        }
                    } else {
                        List<Integer> provinces = new ArrayList<>();
                        List<Integer> cities = new ArrayList<>();
                        List<Integer> districts = new ArrayList<>();
                        for (Integer code : districtCodes) {
                            if (code == DistrictUtil.CHINA_CODE) {
                                param.setHasAll(true);
                                return param;
                            }

                            District district = DistrictUtil.getDistrict(code);
                            if (district != null) {
                                int lvl = district.getLevel();
                                if (lvl == 1) {
                                    provinces.add(code);
                                } else if (lvl == 2) {
                                    cities.add(code);
                                } else if (lvl == 3) {
                                    districts.add(code);
                                }
                            } else {
                                param.setHasPermission(false);
                                break;
                            }
                        }

                        int ps = provinces.size();
                        int cs = cities.size();
                        int ds = districts.size();

                        if (ps == 0 && cs == 0 && ds == 0) {
                            param.setHasPermission(false);
                        } else {
                            if (ps == 1) {
                                param.setProvinceCode(provinces.get(0));
                            } else if (ps > 1) {
                                param.setProvinceCodes(provinces);
                            }

                            if (cs == 1) {
                                param.setCityCode(cities.get(0));
                            } else if (cs > 1) {
                                param.setCityCodes(cities);
                            }

                            if (ds == 1) {
                                param.setDistrictCode(districts.get(0));
                            } else if (ds > 1) {
                                param.setDistrictCodes(districts);
                            }
                        }
                    }
                } else {
                    param.setHasPermission(false);
                }
            } else {
                param.setHasPermission(false);
            }
        } else {
            param.setHasAll(true);
        }
        return param;
    }

    public static boolean hasPermission(String permissionCode) {
        return hasPermission(MonitorUserSession.getCurrentUserSession(), permissionCode);
    }

    public static boolean hasPermission(MonitorUserSession userSession, String permissionCode) {
        if (userSession.isSystemAdmin()) return true;
        String[] roleIds = userSession.getRoleIds();
        if (roleIds == null || roleIds.length == 0) return false;

        PermissionContainer permissionContainer = dataCacheManager.getData(PermissionContainer.class);

        for (String roleId : roleIds) {
            if (permissionContainer.hasPermission(roleId, permissionCode)) {
                return true;
            }
        }
        return false;
    }
}
