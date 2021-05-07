package com.styx.monitor.service.config.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/3/30
 */
public class VariableContainer {

    private Map<Integer, SimpleVariable> variableMap;

    public VariableContainer(List<SimpleVariable> variables) {
        if (variables == null) {
            variableMap = new HashMap<>();
        } else {
            variableMap = new HashMap<>((int) (variables.size() / 0.75 + 1));
            for (SimpleVariable variable : variables) {
                variableMap.put(variable.getId(), variable);
            }
        }
    }

    public SimpleVariable getVariable(int variableId) {
        return variableMap.get(variableId);
    }

    public String getVariableName(int variableId) {
        SimpleVariable variable = variableMap.get(variableId);
        return variable == null ? null : variable.getName();
    }
}
