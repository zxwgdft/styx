package com.styx.common.excel;

/**
 * 属性转化接口
 *
 * @author TontoZhou
 */
public interface PropertyConvert<T> {

    T convert(Object obj) throws ConvertException;
}
