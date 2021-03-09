package com.styx.data.mapper;

import com.styx.data.model.TerminalDataFlow;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/12/23
 */
public interface TerminalDataFlowMapper {

    @Select("SELECT terminal_id AS terminalId, flow_value AS flowValue FROM terminal_data_flow")
    List<TerminalDataFlow> getDataFlow();

    @Update("UPDATE terminal_data_flow SET flow_value = #{value}, update_time = NOW() WHERE terminal_id = #{terminalId}")
    int updateFlowValue(@Param("terminalId") int terminalId, @Param("value") float value);

    @Insert("INSERT terminal_data_flow VALUES(#{terminalId},#{value},NOW())")
    int insertFlowValue(@Param("terminalId") int terminalId, @Param("value") float value);
}
