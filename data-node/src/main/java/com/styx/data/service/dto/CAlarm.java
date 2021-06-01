package com.styx.data.service.dto;

import lombok.Getter;
import lombok.Setter;

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
