package com.styx.monitor.mapper.data;

import com.styx.monitor.service.data.vo.TerminalFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/5/11
 */
public interface TerminalDataMapper {

    List<TerminalFlow> findTerminalListForFlow(@Param("ids") String ids);

}
