package com.paladin.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Variable {

    private Integer id;
    private Integer type;
    private Float min;
    private Float max;
    private Integer startPosition;
    private Integer switchPosition;
    private Float value;
}
