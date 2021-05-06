package com.styx.monitor.service.data.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/4/20
 */
@Getter
@Setter
@AllArgsConstructor
public class AlarmStatus {

    public final static int TYPE_FORMULA = 1;
    public final static int TYPE_VARIABLE = 2;

    // 报警的ID或变量ID，根据类型而定
    private int id;
    // 报警类型（报警公式触发，还是变量触发）
    private int type;
    // 第一次触发时间
    private long startTime;
    // 关闭时间
    private long closeTime;
}