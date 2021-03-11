package com.paladin.monitor.core;


import com.paladin.monitor.core.config.CTerminal;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.distrcit.District;
import com.paladin.monitor.core.distrcit.DistrictContainer;
import com.paladin.monitor.model.org.OrgUser;
import com.paladin.monitor.model.config.ConfigStation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataPermissionUtil {


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

                        if (code == DistrictContainer.CHINA_CODE) {
                            param.setHasAll(true);
                            return param;
                        }

                        District district = DistrictContainer.getDistrict(code);
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
                            if (code == DistrictContainer.CHINA_CODE) {
                                param.setHasAll(true);
                                return param;
                            }

                            District district = DistrictContainer.getDistrict(code);
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

    public static boolean hasStationPermission(MonitorUserSession userSession, CTerminal terminal) {
        if (userSession.isSystemAdmin()) {
            return true;
        }

        if (terminal == null) return false;

        int userType = userSession.getUserType();
        if (userType == OrgUser.USER_TYPE_APP_ADMIN) {
            return true;
        } else if (userType == OrgUser.USER_TYPE_PERSONNEL) {
            return false;
        } else if (userType == OrgUser.USER_TYPE_STATION) {
            int stationId = terminal.getStationId();
            Integer[] stations = userSession.getStations();
            if (stations != null && stations.length > 0) {
                for (int station : stations) {
                    if (station == stationId) {
                        return true;
                    }
                }
            }
            return false;
        } else if (userType == OrgUser.USER_TYPE_DISTRICT) {
            if (terminal.isTest()) {
                return false;
            }

            int districtCode = terminal.getDistrictCode();
            int provinceCode = terminal.getProvinceCode();
            int cityCode = terminal.getCityCode();

            Integer[] ownDistrictCodes = userSession.getDistricts();
            for (int ownCode : ownDistrictCodes) {
                if (ownCode == provinceCode || ownCode == cityCode || ownCode == districtCode) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasStationPermission(MonitorUserSession userSession, CTerminalContainer.SimpleStation station) {
        if (userSession.isSystemAdmin()) {
            return true;
        }

        if (station == null) return false;

        int userType = userSession.getUserType();
        if (userType == OrgUser.USER_TYPE_APP_ADMIN) {
            return true;
        } else if (userType == OrgUser.USER_TYPE_PERSONNEL) {
            return false;
        } else if (userType == OrgUser.USER_TYPE_STATION) {
            int stationId = station.getId();
            Integer[] ids = userSession.getStations();
            if (ids != null && ids.length > 0) {
                for (int id : ids) {
                    if (id == stationId) {
                        return true;
                    }
                }
            }
            return false;
        } else if (userType == OrgUser.USER_TYPE_DISTRICT) {
            if (station.isTest()) {
                return false;
            }

            int districtCode = station.getDistrictCode();
            int provinceCode = station.getProvinceCode();
            int cityCode = station.getCityCode();

            Integer[] ownDistrictCodes = userSession.getDistricts();
            for (int ownCode : ownDistrictCodes) {
                if (ownCode == provinceCode || ownCode == cityCode || ownCode == districtCode) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasStationPermission(MonitorUserSession userSession, ConfigStation station) {
        if (userSession.isSystemAdmin()) {
            return true;
        }

        if (station == null) return false;

        int userType = userSession.getUserType();
        if (userType == OrgUser.USER_TYPE_APP_ADMIN) {
            return true;
        } else if (userType == OrgUser.USER_TYPE_PERSONNEL) {
            return false;
        } else if (userType == OrgUser.USER_TYPE_STATION) {
            int stationId = station.getId();
            Integer[] ids = userSession.getStations();
            if (ids != null && ids.length > 0) {
                for (int id : ids) {
                    if (id == stationId) {
                        return true;
                    }
                }
            }
            return false;
        } else if (userType == OrgUser.USER_TYPE_DISTRICT) {
            if (station.getIsTest()) {
                return false;
            }

            int districtCode = station.getDistrictCode();
            int provinceCode = station.getProvinceCode();
            int cityCode = station.getCityCode();

            Integer[] ownDistrictCodes = userSession.getDistricts();
            for (int ownCode : ownDistrictCodes) {
                if (ownCode == provinceCode || ownCode == cityCode || ownCode == districtCode) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasDistrictPermission(Integer[] ownDistrictCodes, District targetDistrict) {
        int level = targetDistrict.getLevel();
        if (level == 1) {
            int provinceCode = targetDistrict.getId();
            for (Integer code : ownDistrictCodes) {
                if (code == provinceCode) {
                    return true;
                }
            }
        } else if (level == 2) {
            int cityCode = targetDistrict.getId();
            int provinceCode = targetDistrict.getParent().getId();
            for (Integer code : ownDistrictCodes) {
                if (code == cityCode || code == provinceCode) {
                    return true;
                }
            }
        } else {
            int districtCode = targetDistrict.getId();
            int cityCode = targetDistrict.getParent().getId();
            int provinceCode = targetDistrict.getParent().getParent().getId();
            for (Integer code : ownDistrictCodes) {
                if (code == districtCode || code == cityCode || code == provinceCode) {
                    return true;
                }
            }
        }
        return false;
    }

}
