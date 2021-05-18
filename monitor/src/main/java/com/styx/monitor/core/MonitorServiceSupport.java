package com.styx.monitor.core;

import com.styx.common.api.BaseModel;
import com.styx.common.api.DeletedBaseModel;
import com.styx.common.service.ServiceSupport;
import com.styx.common.service.mybatis.CommonMapper;

import java.io.Serializable;
import java.util.Date;

/**
 * @author TontoZhou
 * @since 2021/4/9
 */
public class MonitorServiceSupport<Model, Mapper extends CommonMapper<Model>> extends ServiceSupport<Model, Mapper> {

    public void save(Model model) {
        if (isBaseModel) {
            Date now = new Date();
            BaseModel baseModel = ((BaseModel) model);
            baseModel.setCreateTime(now);
            baseModel.setUpdateTime(now);
            String userId = MonitorUserSession.getCurrentUserSession().getUserId();
            baseModel.setCreateBy(userId);
            baseModel.setUpdateBy(userId);
        }
        sqlMapper.insert(model);
    }

    public boolean updateWhole(Model model) {
        if (isBaseModel) {
            BaseModel baseModel = ((BaseModel) model);
            baseModel.setUpdateTime(new Date());
            baseModel.setUpdateBy(MonitorUserSession.getCurrentUserSession().getUserId());
        }
        return sqlMapper.updateWholeById(model) > 0;
    }

    public boolean updateSelection(Model model) {
        if (isBaseModel) {
            BaseModel baseModel = ((BaseModel) model);
            baseModel.setUpdateTime(new Date());
            baseModel.setUpdateBy(MonitorUserSession.getCurrentUserSession().getUserId());
        }
        return sqlMapper.updateById(model) > 0;
    }

    public boolean deleteById(Serializable id) {
        if (isDeletedModel) {
            // 逻辑删除实现
            DeletedBaseModel baseModel = new DeletedBaseModel();
            baseModel.setDeleted(true);
            baseModel.setUpdateTime(new Date());
            baseModel.setUpdateBy(MonitorUserSession.getCurrentUserSession().getUserId());
            return sqlMapper.logicDeleteById(baseModel, id) > 0;
        } else {
            return sqlMapper.deleteById(id) > 0;
        }
    }

}
