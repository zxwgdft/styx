package com.styx.data.mapper;

import com.styx.common.service.mybatis.CommonMapper;
import com.styx.data.model.TerminalAlarm;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
public interface TerminalAlarmMapper extends CommonMapper<TerminalAlarm> {

    @Select("SELECT alarm_id AS alarmId, terminal_id AS terminalId, start_time AS startTime FROM terminal_alarm WHERE terminal_id = #{id}")
    List<TerminalAlarm> getAlarmIdOfTerminal(@Param("id") int id);

    @Select("SELECT start_time FROM terminal_alarm WHERE terminal_id = #{tid} AND alarm_id = #{aid}")
    Date getStartTimeOfAlarm(@Param("tid") int tid, @Param("aid") int aid);

    @Delete("DELETE FROM terminal_alarm WHERE terminal_id = #{tid} AND alarm_id = #{aid}")
    int deleteAlarm(@Param("tid") int tid, @Param("aid") int aid);

    @Delete("DELETE FROM terminal_alarm WHERE terminal_id = #{tid}")
    int deleteAlarmByTerminal(@Param("tid") int tid);
}
