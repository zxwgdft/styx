package com.styx.data.mapper;

import com.styx.data.core.terminal.AlarmStatus;
import com.styx.data.core.terminal.Terminal;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
public interface TerminalAlarmMapper {

    @Select("SELECT alarm_id AS id, start_time AS startTime FROM terminal_alarm WHERE terminal_id = #{id}")
    List<AlarmStatus> getAlarmIdOfTerminal(@Param("id") int id);

    @Delete("DELETE FROM terminal_alarm WHERE terminal_id = #{tid} AND alarm_id = #{aid}")
    int deleteAlarm(@Param("tid") int tid, @Param("aid") int aid);

}
