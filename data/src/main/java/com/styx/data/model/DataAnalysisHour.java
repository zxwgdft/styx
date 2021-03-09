package com.styx.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

/**
 * @author MyKite
 * @date 2020/11/19 10:31
 */
@Getter
@Setter
public class DataAnalysisHour {

    @Id
    private Integer day;//天数
    @Id
    private Integer hour;//小时
    @Id
    private Integer terminalId;//终端id
    @Id
    private Integer varId;//变量id

    private Float maxValue;//最大值
    private Float minValue;//最小值
    private Float avgValue;//平均值
    
}
