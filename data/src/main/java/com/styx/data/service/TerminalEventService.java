package com.styx.data.service;

import com.styx.common.utils.convert.JsonUtil;
import com.styx.data.core.terminal.AlarmStatus;
import com.styx.data.core.terminal.Terminal;
import com.styx.data.core.terminal.TerminalListener;
import com.styx.common.config.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/5/10
 */
@Slf4j
@Service
public class TerminalEventService implements TerminalListener {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private SendCallback defaultCallback;

    private TerminalEventService() {
        defaultCallback = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {

            }

            @Override
            public void onException(Throwable e) {
                log.error("发送消息异常", e);
            }
        };
    }


    public void terminalOnline(Terminal terminal) {
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_TERMINAL_EVENT,
                new TerminalEvent(terminal.getId(), MQConstants.TERMINAL_EVENT_ONLINE), defaultCallback);
    }

    public void terminalOffline(Terminal terminal) {
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_TERMINAL_EVENT,
                new TerminalEvent(terminal.getId(), MQConstants.TERMINAL_EVENT_OFFLINE), defaultCallback);

    }

    public void alarmTriggerHandle(Terminal terminal, List<AlarmStatus> alarmStatuses) throws Exception {
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_TERMINAL_EVENT,
                new TerminalEvent(terminal.getId(), MQConstants.TERMINAL_EVENT_ALARM_TRIGGER, JsonUtil.getJson(alarmStatuses)), defaultCallback);

    }

    public void alarmClosedHandle(Terminal terminal, List<AlarmStatus> alarmStatuses) throws Exception {
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_TERMINAL_EVENT,
                new TerminalEvent(terminal.getId(), MQConstants.TERMINAL_EVENT_ALARM_CLOSED, JsonUtil.getJson(alarmStatuses)), defaultCallback);
    }


}
