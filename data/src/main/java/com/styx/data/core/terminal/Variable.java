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
    private int type;
    //数据byte位置
    private int bytePosition;
    //数据bit位置
    private int bitPosition;
    //是否持久化
    private boolean persisted;


}
