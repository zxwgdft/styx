package com.styx.data.service.dto;

import com.styx.data.model.TerminalAlarm;
import com.styx.data.model.TerminalInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/1/15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoveTerminalInfo {

    private Integer terminalId;
    private List<TerminalAlarm> terminalAlarms;
    private TerminalInfo terminalInfo;

}
