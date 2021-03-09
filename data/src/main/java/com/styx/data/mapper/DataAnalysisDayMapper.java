package com.styx.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.styx.data.model.DataAnalysisDay;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author MyKite
 * @date 2020/11/19 10:48
 */
public interface DataAnalysisDayMapper extends BaseMapper<DataAnalysisDay> {

    // 查询统计月报数据并插入
    int analyzeAndInsert(@Param("date") int date);

    List<DataAnalysisDay> findDataForUpload(@Param("limit") int limit);

    int updateUploaded(@Param("limit") int limit);

    @Select("SELECT COUNT(1) FROM data_analysis_day WHERE uploaded = 0")
    int countForUpload();

}
