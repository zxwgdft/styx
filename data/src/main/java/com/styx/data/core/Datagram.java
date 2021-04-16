package com.styx.data.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author TontoZhou
 * @since 2020/10/23
 */
@Getter
@AllArgsConstructor
public class Datagram {

    private String uid;
    private int command;
    private LocalDateTime time;
    private byte[] head;
    private byte[] data;



}
