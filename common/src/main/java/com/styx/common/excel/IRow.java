package com.styx.common.excel;

public interface IRow {
    ICell getCell(int cellIndex);

    int getLastCellNum();
}
