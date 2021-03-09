package com.styx.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.data.model.TerminalDataDetail;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
public interface TerminalDataDetailMapper extends BaseMapper<TerminalDataDetail> {

    int insertBatch(List<TerminalDataDetail> list);

}
