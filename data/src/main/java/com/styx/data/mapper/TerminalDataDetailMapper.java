package com.styx.data.mapper;

import com.styx.data.model.TerminalDataDetail;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
public interface TerminalDataDetailMapper {

    int insertBatch(List<TerminalDataDetail> list);

}
