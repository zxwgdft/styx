package com.styx.common.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.styx.common.api.BaseModel;
import com.styx.common.api.DeletedBaseModel;
import com.styx.common.service.annotation.IgnoreSelection;
import com.styx.common.utils.convert.SimpleBeanCopyUtil;
import com.styx.common.utils.reflect.Entity;
import com.styx.common.utils.reflect.EntityField;
import com.styx.common.utils.reflect.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

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
    protected boolean isBaseModel;
    protected boolean isDeletedModel;

    public ServiceSupport() {
        // 获取泛型类，该泛型类应该是对应数据库某表的实体类类型
        Class<?> clazz = ReflectUtil.getSuperClassArgument(this.getClass(), ServiceSupport.class, 0);
        if (clazz == null) {
            log.warn("实现类[" + this.getClass().getName() + "]没有明确定义["
                    + ServiceSupport.class.getName() + "]的泛型，无法为其注册BaseMapper");
        }
        modelType = (Class<Model>) clazz;
    }


    protected void init() {
        isBaseModel = BaseModel.class.isAssignableFrom(modelType);
        isDeletedModel = DeletedBaseModel.class.isAssignableFrom(modelType);
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


    // -------------------------
    // save update delete
    // -------------------------

    public Model get(Serializable id) {
        return baseMapper.selectById(id);
    }

    public void save(Model model) {
        if (isBaseModel) {
            Date now = new Date();
            BaseModel baseModel = ((BaseModel) model);
            baseModel.setCreateTime(now);
            baseModel.setUpdateTime(now);
        }
        baseMapper.insert(model);
    }

    public boolean update(Model model) {
        return baseMapper.updateById(model) > 0;
    }

    public boolean removeById(Serializable id) {
        // 逻辑删除实现
        return baseMapper.deleteById(id) > 0;
    }


    // -------------------------
    // select 部分
    // -------------------------

    /**
     * 构建同步查询部分
     */
    private Wrapper buildCommon(Wrapper queryWrapper) {
        return queryWrapper;
    }

    /**
     * 构建select部分（当返回对象不为当前model）
     */
    private Wrapper buildSelection(Wrapper queryWrapper, Class<?> clazz) {
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper();
        }
        String[] selections = selectionCacheMap.get(clazz);
        if (selections == null) {
            List<String> list = new ArrayList<>();
            for (EntityField entityField : Entity.getEntity(clazz).getEntityFields()) {
                if (entityField.getAnnotation(IgnoreSelection.class) == null) {
                    String fieldName = entityField.getName();
                    if (Entity.getEntity(modelType).getEntityField(fieldName) != null) {
                        list.add(fieldName);
                    }
                }
            }
            selections = list.toArray(new String[list.size()]);
            selectionCacheMap.put(clazz, selections);
        }

        if (queryWrapper instanceof Query) {
            ((Query) queryWrapper).select(selections);
        }
        return queryWrapper;
    }

    /**
     * 查找所有结果集合
     */
    public List<Model> findList() {
        return searchAll(modelType, null);
    }

    /**
     * 查找所有结果集合
     */
    public List<Model> findList(Wrapper queryWrapper) {
        return searchAll(modelType, queryWrapper);
    }

    /**
     * 查找所有结果集合
     */
    public List<Model> findList(Object queryParam) {
        Wrapper queryWrapper = queryParam != null ?
                QueryWrapperHelper.buildQuery(queryParam) : null;
        return searchAll(modelType, queryWrapper);
    }

    /**
     * 查找所有结果集合
     */
    public <T> List<T> findList(Class<T> clazz) {
        return searchAll(clazz, null);
    }

    /**
     * 查找所有结果集合
     */
    public <T> List<T> findList(Class<T> clazz, Object queryParam) {
        Wrapper queryWrapper = queryParam != null ?
                QueryWrapperHelper.buildQuery(queryParam) : null;
        return searchAll(clazz, queryWrapper);
    }

    /**
     * 查询结果集
     * <p>
     * 会构建通用的sql条件和select部分
     *
     * @param clazz        需要返回的对象类
     * @param queryWrapper 查询条件
     * @return 返回查询结果集
     */
    public <T> List<T> searchAll(Class<T> clazz, Wrapper queryWrapper) {
        queryWrapper = buildCommon(queryWrapper);

        if (modelType != clazz) {
            queryWrapper = buildSelection(queryWrapper, clazz);
            List<Model> result = baseMapper.selectList(queryWrapper);
            if (result != null && result.size() > 0) {
                // mybatis plus baseMapper 没有提供改变返回对象的方法，这里
                // 使用bean copy方法，增加了一些性能损耗。
                return SimpleBeanCopyUtil.simpleCopyList(result, clazz);
            } else {
                return Collections.EMPTY_LIST;
            }
        } else {
            return baseMapper.selectList(queryWrapper);
        }
    }

    /**
     * 查找记录数
     */
    public int findCount(Object queryParam) {
        Wrapper queryWrapper = queryParam != null ?
                QueryWrapperHelper.buildQuery(queryParam) : null;
        return searchCount(queryWrapper);
    }

    /**
     * 查询记录数
     *
     * @param queryWrapper 查询条件
     * @return 记录数
     */
    public int searchCount(Wrapper queryWrapper) {
        queryWrapper = buildCommon(queryWrapper);
        return baseMapper.selectCount(queryWrapper);
    }

    /**
     * 查找分页结果集合
     */
    public PageResult<Model> findPage(PageParam pageParam) {
        return searchPage(modelType, pageParam, null);
    }


    /**
     * 查找分页结果集合
     */
    public <T> PageResult<T> findPage(Class<T> clazz, PageParam pageParam) {
        return searchPage(clazz, pageParam, null);
    }

    /**
     * 查找分页结果集合
     */
    public <T> PageResult<T> findPage(Class<T> clazz, PageParam pageParam, Object queryParam) {
        Wrapper queryWrapper = queryParam != null ?
                QueryWrapperHelper.buildQuery(queryParam) : null;
        return searchPage(clazz, pageParam, queryWrapper);
    }

    /**
     * 查询分页结果集
     *
     * @param clazz        需要返回的对象类
     * @param pageParam    分页参数
     * @param queryWrapper 查询条件
     * @return 返回查询的分页结果集
     */
    public <T> PageResult<T> searchPage(Class<T> clazz, PageParam pageParam, Wrapper queryWrapper) {
        Page<T> page = PageHelper.offsetPage(pageParam.getOffset(), pageParam.getLimit());
        List<T> result = searchAll(clazz, queryWrapper);
        if (result == null || result.size() == 0) {
            return PageResult.getEmptyPageResult(pageParam.getLimit());
        }
        return new PageResult<>(page, result);
    }


}
