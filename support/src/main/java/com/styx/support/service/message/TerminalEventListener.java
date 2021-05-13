package com.styx.support.service.message;

import com.styx.common.config.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author TontoZhou
 * @since 2021/5/12
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = MQConstants.TOPIC_TERMINAL_EVENT, consumerGroup = MQConstants.CONSUMER_GROUP_SUPPORT)
public class TerminalEventListener implements RocketMQListener<TerminalEvent> {

    @Override
    public void onMessage(TerminalEvent message) {
        int event = message.getEvent();
        switch (event) {
            case MQConstants.TERMINAL_EVENT_ONLINE:
                onlineHandle(message);
                break;
            case MQConstants.TERMINAL_EVENT_OFFLINE:
                offlineHandle(message);
                break;
            case MQConstants.TERMINAL_EVENT_ALARM_TRIGGER:
                alarmTriggerHandle(message);
                break;
            case MQConstants.TERMINAL_EVENT_ALARM_CLOSED:
                alarmClosedHandle(message);
                break;
        }
    }

    private void onlineHandle(TerminalEvent message) {
        log.info("终端{}在时间{}上线", message.getId(), message.getTime());
    }

    private void offlineHandle(TerminalEvent message) {
        log.info("终端{}在时间{}下线", message.getId(), message.getTime());
    }

    private void alarmTriggerHandle(TerminalEvent message) {
        log.info("终端{}在时间{}触发报警", message.getId(), message.getTime());
    }

    private void alarmClosedHandle(TerminalEvent message) {
        log.info("终端{}在时间{}关闭报警", message.getId(), message.getTime());
    }
}
