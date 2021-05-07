package com.styx.monitor.service.config.cache;

import com.styx.monitor.model.config.ConfigVariable;
import com.styx.monitor.model.config.ConfigVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/3/30
 */
public class VariableContainer {

    private Map<Integer, ConfigVariable> variableMap;

    public VariableContainer(List<ConfigVariable> variables) {
        if (variables == null) {
            variableMap = new HashMap<>();
        } else {
            variableMap = new HashMap<>((int) (variables.size() / 0.75 + 1));
            for (ConfigVariable variable : variables) {
                variableMap.put(variable.getId(), variable);
            }
        }
    }

    public ConfigVariable getVariable(int variableId) {
        return variableMap.get(variableId);
    }

    public String getVariableName(int variableId) {
        ConfigVariable variable = variableMap.get(variableId);
        return variable == null ? null : variable.getName();
    }
}
