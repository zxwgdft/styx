package com.styx.common.api;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TontoZhou
 * @since 2019/12/10
 */
@Getter
@AllArgsConstructor
@ApiModel(description = "feign code")
public class FeignCode implements IResultCode {
    private int code;
    private String message;
}
