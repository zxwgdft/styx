package com.styx.data.model;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/1/4
 */
@Getter
@Setter
public class TerminalDataFlow {

    @TableId
    private Integer terminalId;
    private Float flowValue;

}
