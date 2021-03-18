package com.styx.common.utils.support;

import java.io.Serializable;

/**
 * @author TontoZhou
 * @since 2021/3/17
 */
@FunctionalInterface
public interface GetFunction<T> extends Serializable {
    Object apply(T t);
}
