package com.styx.monitor.service.config.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author TontoZhou
 * @since 2020/11/4
 */
@Getter
@Setter
public class CAlarm {

    private int id;
    private String name;
    private String formula;
    private String variableIds;

}
