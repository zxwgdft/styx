package com.styx.common.excel.read;

import com.styx.common.excel.ICell;

public interface ReadPropertyConvert<T> {
	public T convert(ICell cell) throws ExcelReadException;
	
}
