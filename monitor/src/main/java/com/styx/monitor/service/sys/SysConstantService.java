package com.styx.monitor.service.sys;

import com.styx.common.cache.DataCache;
import com.styx.monitor.mapper.sys.SysConstantMapper;
import com.styx.monitor.model.sys.SysConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/4/12
 */
@Service
public class SysConstantService implements DataCache<SysConstantService.ConstantContainer> {

    @Autowired
    private SysConstantMapper sysConstantMapper;

    public String getId() {
        return "CONSTANT_CACHE";
    }

    @Override
    public ConstantContainer loadData() {

        List<SysConstant> constants = sysConstantMapper.findList();


        return null;
    }


    public static class ConstantContainer {

        private String json;



    }

    public static class Constant {

    }

}
