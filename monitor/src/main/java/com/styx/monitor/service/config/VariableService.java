package com.styx.monitor.service.config;

import com.styx.common.cache.DataCacheManager;
import com.styx.monitor.core.MonitorServiceSupport;
import com.styx.monitor.model.config.ConfigVariable;
import com.styx.monitor.service.config.cache.VariableContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class VariableService extends MonitorServiceSupport<ConfigVariable> {

    @Autowired
    private DataCacheManager cacheManager;

    public List<ConfigVariable> getEnabledVariables(String variableIds) {
        if (variableIds != null && variableIds.length() > 0) {
            String[] ids = variableIds.split(",");
            VariableContainer variableContainer = cacheManager.getData(VariableContainer.class);
            if (variableContainer != null) {
                List<ConfigVariable> variables = new ArrayList<>(ids.length);
                for (String id : ids) {
                    ConfigVariable variable = variableContainer.getVariable(Integer.valueOf(id));
                    if (variable != null && variable.getEnabled()) {
                        variables.add(variable);
                    }
                }
                return variables;
            }
        }
        return Collections.emptyList();
    }


}
