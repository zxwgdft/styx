package com.styx.common.service.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.styx.common.api.DeletedBaseModel;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;

/**
 * 基于mybatis plus扩展出自己的通用Mapper
 *
 * @author TontoZhou
 * @since 2021/4/9
 */
public interface CommonMapper<Model> extends BaseMapper<Model> {

    String PARAM_ID = "id";
    String PARAM_DBM = "dbm";

    String METHOD_LOGIC_DELETE_BY_ID = "logicDeleteById";
    String METHOD_UPDATE_SELECTION_BY_ID = "updateSelectionById";
    /**
     * 默认mybatis plus有逻辑删除功能，但是没法同时把删除时间和删除用户的信息更新到表中，下面方法因此而生
     */
    int logicDeleteById(@Param(PARAM_DBM) DeletedBaseModel model, @Param(PARAM_ID) Serializable id);


    int updateSelectionById(@Param(Constants.ENTITY) T entity);
}
