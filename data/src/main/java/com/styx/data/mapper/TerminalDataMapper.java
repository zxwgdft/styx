package com.styx.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.data.model.TerminalData;
import com.styx.data.service.dto.Data4Upload;
import com.styx.data.service.dto.DataRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
public interface TerminalDataMapper extends BaseMapper<TerminalData> {

    List<DataRecord> findTerminalData(@Param("terminalId") int terminalId, @Param("startDay") int startDay, @Param("endDay") int endDay, @Param("vids") List<Integer> vids);

    int deleteTestData(@Param("terminalIds") List<Integer> terminalIds);

    List<Data4Upload> findTerminalData4Upload(@Param("index") int index, @Param("limit") int limit);

    int insertUploadDataBatch(@Param("list") List<Data4Upload> data);

    @Select("SELECT (MAX(b.value) - MIN(b.value) ) AS val FROM terminal_data a INNER JOIN terminal_data_detail b ON a.id = b.data_id WHERE a.`day` >= #{startDay} AND a.`day` <= #{endDay} AND a.terminal_id = #{terminalId} AND b.var_id = #{varId}")
    Float getBalanceOfTerminalByTime(@Param("terminalId") int terminalId, @Param("startDay") int startDay, @Param("endDay") int endDay, @Param("varId") int varId);

}
