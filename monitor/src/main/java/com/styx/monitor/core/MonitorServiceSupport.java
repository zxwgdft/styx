package com.styx.monitor.core;

import com.styx.common.api.BaseModel;
import com.styx.common.api.DeletedBaseModel;
import com.styx.common.service.ServiceSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * @author TontoZhou
 * @since 2021/4/9
 */
public class MonitorServiceSupport<Model> extends ServiceSupport<Model> {

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
        commonMapper.insert(model);
    }

    public boolean update(Model model) {
        if (isBaseModel) {
            BaseModel baseModel = ((BaseModel) model);
            baseModel.setUpdateTime(new Date());
            baseModel.setUpdateBy(MonitorUserSession.getCurrentUserSession().getUserId());
        }
        return commonMapper.updateById(model) > 0;
    }

    public boolean deleteById(Serializable id) {
        if (isDeletedModel) {
            // 逻辑删除实现
            DeletedBaseModel baseModel = new DeletedBaseModel();
            baseModel.setDeleted(true);
            baseModel.setUpdateTime(new Date());
            baseModel.setUpdateBy(MonitorUserSession.getCurrentUserSession().getUserId());
            return commonMapper.logicDeleteById(baseModel, id) > 0;
        } else {
            return commonMapper.deleteById(id) > 0;
        }
    }

}
