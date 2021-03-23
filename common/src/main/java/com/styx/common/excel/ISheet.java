package com.styx.common.excel;

public interface ISheet {
    IRow getRow(int rowIndex);

    int getLastRowNum();

    interface InstanceCreator<T> {

        public T createInstance();

    }
}
