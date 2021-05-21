package com.styx.monitor.service.data;

import com.styx.common.config.MQConstants;
import com.styx.common.utils.convert.JsonUtil;
import com.styx.monitor.service.data.vo.AlarmStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/5/12
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = MQConstants.TOPIC_TERMINAL_EVENT, consumerGroup = MQConstants.CONSUMER_GROUP_MONITOR)
public class TerminalEventListener implements RocketMQListener<TerminalEvent> {

    @Override
    public void onMessage(TerminalEvent message) {
        int event = message.getEvent();
        try {
            switch (event) {
                case MQConstants.TERMINAL_EVENT_ONLINE:
                    onlineHandle(message);
                    break;
                case MQConstants.TERMINAL_EVENT_OFFLINE:
                    offlineHandle(message);
                    break;
                case MQConstants.TERMINAL_EVENT_ALARM_TRIGGER:
                    alarmTriggerHandle(message, JsonUtil.parseJsonList(message.getData(), AlarmStatus.class));
                    break;
                case MQConstants.TERMINAL_EVENT_ALARM_CLOSED:
                    alarmClosedHandle(message, JsonUtil.parseJsonList(message.getData(), AlarmStatus.class));
                    break;
            }
        } catch (Exception e) {
            log.error("处理终端事件消息失败", e);
        }
    }

    private void onlineHandle(TerminalEvent message) {
        log.info("终端{}在时间{}上线", message.getId(), message.getTime());
    }

    private void offlineHandle(TerminalEvent message) {
        log.info("终端{}在时间{}下线", message.getId(), message.getTime());
    }

    private void alarmTriggerHandle(TerminalEvent message, List<AlarmStatus> alarmStatuses) {
        log.info("终端{}在时间{}触发报警", message.getId(), message.getTime());
    }

    private void alarmClosedHandle(TerminalEvent message, List<AlarmStatus> alarmStatuses) {
        log.info("终端{}在时间{}关闭报警", message.getId(), message.getTime());
    }
}
