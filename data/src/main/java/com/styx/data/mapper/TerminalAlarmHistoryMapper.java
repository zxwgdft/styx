package com.styx.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.data.model.TerminalAlarmHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
public interface TerminalAlarmHistoryMapper extends BaseMapper<TerminalAlarmHistory> {

    @Select("SELECT id,terminal_id AS terminalId,alarm_id AS alarmId,start_time AS startTime,end_time AS endTime,closed,uploaded FROM terminal_alarm_history WHERE uploaded = 0 LIMIT 500")
    List<TerminalAlarmHistory> findNotUploaded();

    @Update("UPDATE terminal_alarm_history SET uploaded = 1 WHERE id = #{id}")
    int updateUploaded(@Param("id") long id);
}
