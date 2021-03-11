package com.paladin.monitor.core.config;

import com.paladin.monitor.mapper.sys.SysConfigOthersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author TontoZhou
 * @since 2020/10/29
 */
@Slf4j
@Component
public class COthersContainer extends ConfigContainer {

    @Autowired
    private SysConfigOthersMapper configOthersMapper;

    private COthers others;

    @Override
    public String getId() {
        return ConfigContainer.CONTAINER_OTHERS;
    }

    @Override
    public void load() {

        Map<String, Object> item = configOthersMapper.getOthersConfig();

        Integer dataPersistInterval = (Integer) item.get("data_persist_interval");

        COthers others = new COthers();
        others.setDataPersistInterval(dataPersistInterval == null ? 12 : dataPersistInterval);


        this.others = others;

        log.info("重新加载其他配置");
    }


    public COthers getOthers() {
        return others;
    }
}
