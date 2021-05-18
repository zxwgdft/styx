package com.styx.monitor.service.config;

import com.styx.common.cache.DataCacheManager;
import com.styx.monitor.core.MonitorServiceSupport;
import com.styx.monitor.mapper.config.ConfigVariableMapper;
import com.styx.monitor.model.config.ConfigVariable;
import com.styx.monitor.service.config.cache.SimpleVariable;
import com.styx.monitor.service.config.cache.VariableContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class VariableService extends MonitorServiceSupport<ConfigVariable, ConfigVariableMapper> {

    @Autowired
    private DataCacheManager cacheManager;

    public List<SimpleVariable> getEnabledVariables(String variableIds) {
        if (variableIds != null && variableIds.length() > 0) {
            String[] ids = variableIds.split(",");
            VariableContainer variableContainer = cacheManager.getData(VariableContainer.class);
            if (variableContainer != null) {
                List<SimpleVariable> variables = new ArrayList<>(ids.length);
                for (String id : ids) {
                    SimpleVariable variable = variableContainer.getVariable(Integer.valueOf(id));
                    if (variable != null && variable.getEnabled()) {
                        variables.add(variable);
                    }
                }
                return variables;
            }
        }
        return Collections.emptyList();
    }


    public List<SimpleVariable> findEnabledSimpleVariable() {
        return getSqlMapper().findEnabledSimpleVariable();
    }
}
