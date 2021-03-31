package com.styx.monitor.core.distrcit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/5/7
 */
@Slf4j
public class DistrictContainer {

    public final static int CHINA_CODE = 1;

    private static Map<Integer, District> districtMap = new HashMap<>();
    private static List<District> rootDistrict;

    static {
        try {
            InputStream input = DistrictContainer.class.getResourceAsStream("district.json");
            rootDistrict = new ObjectMapper().readValue(input, new TypeReference<List<District>>() {
            });
            registerDistrict(rootDistrict);
            initDistrictParent(rootDistrict);
            initDistrictLevel(rootDistrict, 1);
        } catch (IOException e) {
            log.error("读取省、市、区基础数据异常", e);
        }
    }

    private static void registerDistrict(List<District> districts) {
        if (districts != null) {
            for (District district : districts) {
                districtMap.put(district.getId(), district);
                registerDistrict(district.getChildren());
            }
        }
    }

    private static void initDistrictParent(List<District> districts) {
        if (districts != null) {
            for (District district : districts) {
                Integer pid = district.getPid();
                if (pid != null) {
                    District parent = districtMap.get(pid);
                    district.setParent(parent);
                }
                initDistrictParent(district.getChildren());
            }
        }
    }

    private static void initDistrictLevel(List<District> districts, int level) {
        if (districts != null) {
            for (District district : districts) {
                district.setLevel(level);
                initDistrictLevel(district.getChildren(), level + 1);
            }
        }
    }

    public static District getDistrict(int code) {
        return districtMap.get(code);
    }

    public static List<District> getDistricts() {
        return rootDistrict;
    }

    public static String getDistrictName(Integer districtCode) {
        District district = districtMap.get(districtCode);
        if (district != null) {
            return district.getName();
        }
        return null;
    }

    public static String getDistrictFullName(Integer districtCode) {
        District district = districtMap.get(districtCode);
        if (district != null) {
            return district.getFullName();
        }
        return null;
    }
}
