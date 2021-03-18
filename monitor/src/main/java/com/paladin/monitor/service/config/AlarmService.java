package com.paladin.monitor.service.config;

import com.paladin.monitor.core.config.CVariableContainer;
import com.paladin.monitor.core.config.ConfigContainer;
import com.paladin.monitor.core.config.ConfigContainerManager;
import com.paladin.monitor.mapper.config.ConfigAlarmMapper;
import com.paladin.monitor.model.config.ConfigAlarm;
import com.paladin.monitor.service.config.dto.AlarmDTO;
import com.paladin.monitor.service.config.vo.VariableVO;
import com.styx.common.exception.BusinessException;
import com.styx.common.service.ServiceSupport;
import com.styx.common.utils.convert.SimpleBeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AlarmService extends ServiceSupport<ConfigAlarm> {

    @Autowired
    private ConfigAlarmMapper alarmMapper;

    @Autowired
    private CVariableContainer variableContainer;

    @Autowired
    private ConfigContainerManager configContainerManager;

    @Transactional
    public void saveAlarm(AlarmDTO alarmDTO) {
        ConfigAlarm alarm = SimpleBeanCopyUtil.simpleCopy(alarmDTO, new ConfigAlarm());
        alarm.setId(null);

        String varIds = getVariableIds(alarmDTO.getLogic(), alarm.getEnabled());
        alarm.setVariableList(varIds);

        save(alarm);
        configContainerManager.reloadContainer(ConfigContainer.CONTAINER_ALARM);
    }

    @Transactional
    public void updateAlarm(AlarmDTO model) {
        ConfigAlarm alarm = get(model.getId());
        if (alarm == null) {
            throw new BusinessException("找不到需要修改的对象");
        }

        SimpleBeanCopyUtil.simpleCopy(model, alarm);

        String varIds = getVariableIds(model.getLogic(), alarm.getEnabled());
        alarm.setVariableList(varIds);

        update(alarm);
        configContainerManager.reloadContainer(ConfigContainer.CONTAINER_ALARM);
    }

    @Transactional
    public void startAlarm(int id) {
        ConfigAlarm alarm = get(id);
        if (alarm == null) {
            throw new BusinessException("找不到需要启动的对象");
        }

        if (alarm.getEnabled()) {
            return;
        }

        String[] varIds = alarm.getVariableList().split(",");
        for (String varId : varIds) {
            int vid = Integer.valueOf(varId);
            VariableVO variable = variableContainer.getVariable(vid);
            if (variable == null || !variable.getEnabled()) {
                throw new BusinessException("变量[" + id + "]不存在或未启用");
            }
        }

        if (alarmMapper.updateEnabled(id, true) > 0) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_ALARM);
        }
    }

    @Transactional
    public void stopAlarm(int id) {
        if (alarmMapper.updateEnabled(id, false) > 0) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_ALARM);
        }
    }

    @Transactional
    public void removeAlarm(int id) {
        if (removeById(id)) {
            configContainerManager.reloadContainer(ConfigContainer.CONTAINER_ALARM);
        }
    }


    private String getVariableIds(String template, boolean enabled) {
        StringBuilder sb = new StringBuilder();
        if (template != null && template.length() > 0) {
            Pattern pattern = Pattern.compile("value[0-9]+");
            Matcher matcher = pattern.matcher(template);
            while (matcher.find()) {
                String group = matcher.group();
                String varId = group.substring(5);

                if (enabled) {
                    int id = Integer.valueOf(varId);
                    VariableVO variable = variableContainer.getVariable(id);
                    if (variable == null || !variable.getEnabled()) {
                        throw new BusinessException("变量[" + id + "]不存在或未启用");
                    }
                }
                sb.append(varId).append(",");
            }
        }

        if (sb.length() == 0) {
            throw new BusinessException("报警表达式异常，没检测到变量");
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}
