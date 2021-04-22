package com.styx.data.core.terminal;

import com.styx.data.core.Constants;

import java.util.*;

/**
 * @author TontoZhou
 * @since 2021/4/20
 */
public class VariableContainer {

    private long version;
    private List<Variable> variableList;
    private List<Variable> alarmVariableList;
    private Map<Integer, Variable> variableMap;
    private Map<Integer, Variable> alarmVariableMap;

    public VariableContainer(long version, List<Variable> variableList) {
        this.version = version;
        if (variableList == null || variableList.size() == 0) {
            this.variableList = Collections.emptyList();
            this.variableMap = Collections.emptyMap();
            this.alarmVariableMap = Collections.emptyMap();
        } else {
            this.variableList = Collections.unmodifiableList(variableList);

            int initialCapacity = (int) (variableList.size() / .75f) + 1;
            this.variableMap = new HashMap<>(initialCapacity);
            this.alarmVariableMap = new HashMap<>(Math.max(initialCapacity / 2, 16));
            List<Variable> alarmVariableList = new ArrayList<>(initialCapacity / 2);
            for (Variable variable : variableList) {
                variableMap.put(variable.getId(), variable);
                if (variable.getType() == Constants.VALUE_TYPE_FAULT) {
                    alarmVariableMap.put(variable.getId(), variable);
                    alarmVariableList.add(variable);
                }
            }
            this.alarmVariableList = Collections.unmodifiableList(alarmVariableList);
        }
    }

    public Variable getVariable(int id) {
        return variableMap.get(id);
    }

    public long getVersion() {
        return version;
    }

    public List<Variable> getVariables() {
        return variableList;
    }

    public List<Variable> getAlarmVariableList() {
        return alarmVariableList;
    }
}
