package com.styx.monitor.service.sys;

import com.styx.common.cache.DataCache;
import com.styx.common.cache.DataCacheManager;
import com.styx.common.exception.SystemException;
import com.styx.common.utils.convert.JsonUtil;
import com.styx.monitor.mapper.sys.SysConstantMapper;
import com.styx.monitor.model.sys.SysConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/4/12
 */
@Service
public class SysConstantService implements DataCache<SysConstantService.ConstantContainer> {

    @Autowired
    private SysConstantMapper sysConstantMapper;

    @Autowired
    private DataCacheManager source;

    public String getId() {
        return "CONSTANT_CACHE";
    }

    @Override
    public ConstantContainer loadData(long version) {
        List<SysConstant> constants = sysConstantMapper.findList();
        Map<String, Map<String, String>> constantMap = new HashMap<>();
        if (constants != null && constants.size() > 0) {
            String currentType = null;
            Map<String, String> currentCodeMap = null;
            for (SysConstant item : constants) {
                String type = item.getType();
                if (!type.equals(currentType)) {
                    currentCodeMap = new LinkedHashMap<>();
                    constantMap.put(type, currentCodeMap);
                    currentType = type;
                }

                currentCodeMap.put(item.getCode(), item.getName());
            }
        }
        return new ConstantContainer(constantMap, version);
    }


    public static class ConstantContainer {

        private String json;
        private long version;
        private Map<String, Map<String, String>> constantMap;

        private ConstantContainer(Map<String, Map<String, String>> constantMap, long version) {
            this.constantMap = constantMap;
            this.version = version;
            try {
                this.json = JsonUtil.getJson(constantMap);
            } catch (IOException e) {
                throw new SystemException(SystemException.CODE_ERROR_CODE, "", e);
            }
        }

        public long getVersion() {
            return version;
        }

        public String getConstantJson() {
            return json;
        }

        public String getConstantName(String type, String code) {
            Map<String, String> codeMap = constantMap.get(type);
            if (codeMap != null) {
                return codeMap.get(code);
            }
            return null;
        }
    }


    /**
     * 获取常量容器
     */
    public ConstantContainer getConstantContainer() {
        return source.getData(SysConstantService.ConstantContainer.class);
    }


}
