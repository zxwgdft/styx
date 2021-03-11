package com.paladin.monitor.core.config;

import com.paladin.monitor.service.variable.VariableService;
import com.paladin.monitor.service.variable.vo.VariableVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author TontoZhou
 * @since 2020/10/29
 */
@Slf4j
@Component
public class CVariableContainer extends ConfigContainer {

    @Autowired
    private VariableService variableService;
    private List<CVariable> variables;
    private List<VariableVO> showVariables;
    private Set<Integer> showVariableIds;
    private Map<Integer, VariableVO> variableMap;

    @Override
    public String getId() {
        return ConfigContainer.CONTAINER_VARIABLE;
    }

    @Override
    public void load() {
        List<VariableVO> list = variableService.findAll(VariableVO.class);
        List<CVariable> variables = new ArrayList<>(list.size());
        List<VariableVO> showVariables = new ArrayList<>();
        Set<Integer> showVariableIds = new HashSet<>();
        Map<Integer, VariableVO> variableMap = new HashMap<>();

        for (VariableVO item : list) {
            if (item.getEnabled()) {
                CVariable variable = new CVariable();
                variable.setId(item.getId());
                variable.setName(item.getName());
                variable.setValueType(item.getType());
                variable.setAddressStart(item.getStartPosition());

                Integer tag = item.getTag();
                if (tag == null) {
                    variable.setSensorType(-1);
                } else {
                    variable.setSensorType(tag);
                }

                Boolean persisted = item.getPersisted();
                variable.setPersisted(persisted != null && persisted);
                variable.setSwitchAddress(item.getSwitchPosition());

                variable.setMax(item.getMax());
                variable.setMin(item.getMin());

                variables.add(variable);

                Boolean isShow = item.getShowed();
                if (isShow != null && isShow) {
                    showVariables.add(item);
                    showVariableIds.add(item.getId());
                }
            }

            variableMap.put(item.getId(), item);
        }

        this.variables = Collections.unmodifiableList(variables);
        this.showVariables = Collections.unmodifiableList(showVariables);
        this.variableMap = Collections.unmodifiableMap(variableMap);
        this.showVariableIds = showVariableIds;

        log.info("重新加载变量配置");
    }

    public List<CVariable> getCVariables() {
        return variables;
    }

    public List<VariableVO> getShowVariables() {
        return showVariables;
    }

    public VariableVO getVariable(int id) {
        return variableMap.get(id);
    }

    public List<VariableVO> getVariables() {
        return new ArrayList<>(variableMap.values());
    }

    public List<VariableVO> getEnabledVariables() {
        Collection<VariableVO> coll = variableMap.values();
        List<VariableVO> result = new ArrayList<>(coll.size());
        for (VariableVO item : coll) {
            if (item.getEnabled()) {
                result.add(item);
            }
        }
        return result;
    }

    public List<VariableVO> getShowVariableOfTerminal(CTerminal terminal) {
        List<VariableVO> result = new ArrayList<>(showVariables.size());
        String vids = terminal.getVarIds();
        if (vids != null && vids.length() > 0) {
            for (String vs : vids.split(",")) {
                Integer id = Integer.valueOf(vs);
                if (showVariableIds.contains(id)) {
                    result.add(variableMap.get(id));
                }
            }
        }
        return result;
    }

    public List<Integer> getShowVariableIdsOfTerminal(CTerminal terminal) {
        List<Integer> result = new ArrayList<>(showVariables.size());
        String vids = terminal.getVarIds();
        if (vids != null && vids.length() > 0) {
            for (String vs : vids.split(",")) {
                Integer id = Integer.valueOf(vs);
                if (showVariableIds.contains(id)) {
                    result.add(id);
                }
            }
        }
        return result;
    }
}
