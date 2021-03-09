package com.styx.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TerminalDataDetail {
    @Id
    private Long dataId;
    @Id
    private Integer varId;
    private Float value;
}
