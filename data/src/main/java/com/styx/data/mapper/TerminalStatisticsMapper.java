package com.styx.data.mapper;

import com.styx.data.service.vo.VariablePassStatus;
import org.apache.ibatis.annotations.Param;

/**
 * @author TontoZhou
 * @since 2021/1/5
 */
public interface TerminalStatisticsMapper {

    VariablePassStatus getVariablePassNum(@Param("terminalId") int terminalId, @Param("variableId") int variableId,
                                          @Param("startDay") int startDay, @Param("endDay") int endDay, @Param("max") float max, @Param("min") float min);
}
