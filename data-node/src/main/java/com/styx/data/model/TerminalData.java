package com.styx.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author TontoZhou
 * @since 2020/11/13
 */
@Getter
@Setter
public class TerminalData {

    @TableId(type = IdType.AUTO)
    private Long id;
    // 终端ID
    private Integer terminalId;
    // 是否在线
    private Boolean isOnline;
    // 创建时间
    private Date createTime;
    // 日期 20200101
    private Integer day;
    // 小时
    private Integer hour;

}
