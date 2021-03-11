package com.styx.common.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.common.utils.reflect.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/3/11
 */
@Slf4j
public class ServiceSupport<Model> {

    /**
     * 选择项缓存
     */
    private static Map<Class, String[]> selectionCacheMap = new HashMap<>();

    protected Class<Model> modelType; // 业务对应类

    public ServiceSupport() {
        // 获取泛型类，该泛型类应该是对应数据库某表的实体类类型
        Class<?> clazz = ReflectUtil.getSuperClassArgument(this.getClass(), ServiceSupport.class, 0);
        if (clazz == null) {
            log.warn("实现类[" + this.getClass().getName() + "]没有明确定义[" + ServiceSupport.class.getName() + "]的泛型，无法为其注册BaseMapper");
        }
        modelType = (Class<Model>) clazz;
    }

    /**
     * 基于mybatis plus的BaseMapper
     */
    private BaseMapper<Model> baseMapper;

    public BaseMapper<Model> getBaseMapper() {
        return baseMapper;
    }

    public void setBaseMapper(BaseMapper<Model> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public Model get(Serializable id) {
        return baseMapper.selectById(id);
    }

    public void save(Model model) {
        baseMapper.insert(model);
    }

    public boolean update(Model model) {
        return baseMapper.updateById(model) > 0;
    }

    public boolean removeById(Serializable id) {
        return baseMapper.deleteById(id) > 0;
    }


    public void findPage(){
        baseMapper.selectPage()
    }

}
