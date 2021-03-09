package com.styx.data.core.terminal;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author TontoZhou
 * @since 2020/10/26
 */
@Getter
@Setter
public class Variable {

    //变量ID
    private int id;
    //变量名称
    private String name;
    //变量值类型
    private int valueType;
    //变量标识，传感器地址码
    private int sensorType;
    //数据地址
    private int addressStart;
    //开关占位地址
    private int switchAddress;
    //是否持久化
    private boolean persisted;

    private Float max;
    private Float min;

}
