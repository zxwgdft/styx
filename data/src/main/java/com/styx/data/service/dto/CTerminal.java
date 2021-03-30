package com.styx.data.service.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2020/10/29
 */
@Getter
@Setter
public class CTerminal {

    private int id;
    private String uid;
    private int type;
    private String varIds;
    private String alarmIds;

}
