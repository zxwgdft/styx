package com.paladin.monitor.core.config;

/**
 * 数据采集节点配置数据版本控制容器，通过版本变化发送配置数据
 *
 * @author TontoZhou
 * @since 2020/10/30
 */
public abstract class ConfigContainer {

    public final static String CONTAINER_VARIABLE = "container_variable";
    public final static String CONTAINER_ALARM = "container_alarm";
    public final static String CONTAINER_TERMINAL = "container_terminal";
    public final static String CONTAINER_OTHERS = "container_others";

    private long version;

    public abstract String getId();

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void addVersion() {
        version++;
    }

    public abstract void load();

    public void reload() {
        load();
    }
}
