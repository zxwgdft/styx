package com.paladin.monitor.service.data;

import com.paladin.monitor.service.config.vo.VariableVO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TontoZhou
 * @since 2020/12/18
 */
public class DataUtil {

    /**
     * 获取终端可显示变量列表
     */
    public static List<Integer> getShowVariableId(String vids, List<VariableVO> allShowVariables) {
        List<Integer> variableIds = new ArrayList<>(allShowVariables.size());
        if (vids != null && vids.length() > 0) {
            String[] arr = vids.split(",");
            Set<String> set = new HashSet<>();
            for (String str : arr) {
                set.add(str);
            }

            for (VariableVO var : allShowVariables) {
                int vid = var.getId();
                if (set.contains(String.valueOf(vid))) {
                    variableIds.add(vid);
                }
            }
        }
        return variableIds;
    }

    /**
     * 获取终端可显示变量列表（基于选择的变量）
     */
    public static List<Integer> getShowVariableId(String vids, List<VariableVO> allShowVariables, List<Integer> selectVids) {
        List<Integer> variableIds = new ArrayList<>(allShowVariables.size());
        if (vids != null && vids.length() > 0) {
            String[] arr = vids.split(",");
            Set<String> set = new HashSet<>();
            for (String str : arr) {
                set.add(str);
            }

            for (VariableVO var : allShowVariables) {
                int vid = var.getId();
                if (set.contains(String.valueOf(vid))) {
                    if (selectVids != null && selectVids.size() > 0) {
                        for (Integer selectVid : selectVids) {
                            if (selectVid == vid) {
                                variableIds.add(vid);
                                break;
                            }
                        }
                    } else {
                        variableIds.add(vid);
                    }
                }
            }
        }
        return variableIds;
    }
}
