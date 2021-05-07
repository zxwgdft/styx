package com.styx.monitor.service.config.cache;

import com.styx.common.cache.DataCache;
import com.styx.monitor.model.config.ConfigVariable;
import com.styx.monitor.service.config.VariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/3/30
 */
@Component
public class VariableDataCache implements DataCache<VariableContainer> {

    @Autowired
    private VariableService variableService;

    public String getId() {
        return "VARIABLE_CACHE";
    }

    @Override
    public VariableContainer loadData(long version) {
        List<SimpleVariable> variables = variableService.findEnabledSimpleVariable();
        return new VariableContainer(variables);
    }
}
