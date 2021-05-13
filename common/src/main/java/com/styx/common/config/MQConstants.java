package com.styx.common.config;

/**
 * @author TontoZhou
 * @since 2021/5/12
 */
public interface MQConstants {

    // rocketMQ topic
    String TOPIC_TERMINAL_EVENT = "terminal_event";

    // rocketMQ consumer group
    String CONSUMER_GROUP_MONITOR = "styx-monitor";
    String CONSUMER_GROUP_SUPPORT = "styx-support";

    // 终端事件
    int TERMINAL_EVENT_ONLINE = 1001;
    int TERMINAL_EVENT_OFFLINE = 1002;
    int TERMINAL_EVENT_ALARM_TRIGGER = 1003;
    int TERMINAL_EVENT_ALARM_CLOSED = 1004;

}
