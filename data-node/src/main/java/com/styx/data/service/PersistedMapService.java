package com.styx.data.service;

import com.styx.common.utils.StringUtil;
import com.styx.common.utils.convert.JsonUtil;
import com.styx.data.mapper.SysMapMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 使用时需要考虑同步问题，是否存在单机或分布式下的并发问题
 * <p>
 * 通常情况是用于定时任务，则只需保持同时只有一个任务运行
 *
 * @author TontoZhou
 * @since 2021/4/28
 */
@Slf4j
@Service
public class PersistedMapService {

    @Autowired
    private SysMapMapper sysMapMapper;


    public void putObject(String key, Object value) throws IOException {
        String json = JsonUtil.getJson(value);
        putText(key, json);
    }

    public void putText(String key, String value) {
        if (sysMapMapper.updateTextValue(key, value) == 0) {
            try {
                sysMapMapper.insertText(key, value);
            } catch (DuplicateKeyException e) {
                // 主键冲突，已经存在数据，则再次更新
                sysMapMapper.updateTextValue(key, value);
            }
        }
    }

    public String getText(String key) {
        return sysMapMapper.getTextValue(key);
    }

    public <T> T getObject(String key, Class<T> clazz) throws IOException {
        String json = sysMapMapper.getTextValue(key);
        if (!StringUtil.isEmpty(json)) {
            return JsonUtil.parseJson(json, clazz);
        }
        return null;
    }


    public <T> List<T> getObjectList(String key, Class<T> clazz) throws IOException {
        String json = sysMapMapper.getTextValue(key);
        if (!StringUtil.isEmpty(json)) {
            return JsonUtil.parseJsonList(json, clazz);
        }
        return null;
    }
}
