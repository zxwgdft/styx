package com.styx.common.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.common.spring.SpringBeanHelper;
import com.styx.common.utils.reflect.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 业务支持类容器，启动时为{@link ServiceSupport}自动注入SqlMapper
 *
 * @author TontoZhou
 */
@Slf4j
public class ServiceSupportManager implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {

        /**
         * 根据泛型类型为service support注入相应的sqlMapper
         */

        Map<String, BaseMapper> BaseMappers = SpringBeanHelper.getBeansByType(BaseMapper.class);
        Map<String, ServiceSupport> serviceSupports = SpringBeanHelper.getBeansByType(ServiceSupport.class);

        Map<Class<?>, BaseMapper> mapperMap = new HashMap<>();

        for (Entry<String, BaseMapper> entry : BaseMappers.entrySet()) {
            BaseMapper mapper = entry.getValue();
            Class<?> genericType = ReflectUtil.getSuperClassArgument(mapper.getClass(), BaseMapper.class, 0);

            if (genericType == null || genericType == Object.class) {
                log.warn("[" + mapper.getClass().getName() + "]的实现类没有明确定义[" + BaseMapper.class.getName() + "]的泛型");
                continue;
            }

            BaseMapper oldMapper = mapperMap.get(genericType);
            if (oldMapper != null)
                log.warn("实体类[" + genericType.getName() + "]存在多个BaseMapper实现类，[" + oldMapper.getClass().getName() + "]将被覆盖");

            mapperMap.put(genericType, mapper);
        }

        for (Entry<String, ServiceSupport> entry : serviceSupports.entrySet()) {
            ServiceSupport support = entry.getValue();
            Class<?> genericType = ReflectUtil.getSuperClassArgument(support.getClass(), ServiceSupport.class, 0);

            if (genericType == null || genericType == Object.class) {
                log.warn("[" + support.getClass().getName() + "]的实现类没有明确定义[" + ServiceSupport.class.getName() + "]的泛型，无法为其注册BaseMapper");
                continue;
            }

            BaseMapper mapper = mapperMap.get(genericType);
            if (mapper == null) {
                log.warn("实体类[" + genericType.getName() + "]没有对应的[" + BaseMapper.class.getName() + "]的实现类");
                continue;
            } else {
                support.setBaseMapper(mapper);
                log.debug("===>为[" + support.getClass().getName() + "]注入BaseMapper");
            }

            support.init();
            log.debug("<===[" + support.getClass().getName() + "]初始化成功");
        }
    }


}
