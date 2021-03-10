package com.styx.data.model;

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
public class TerminalInfo {

    @TableId
    private Integer id;
    // 工作总时间
    private Integer workTotalTime;
    // 最近登录时间
    private Date lastLoginTime;

    private Date updateTime;
    // 维护结束时间
    private Long maintainOffTime;

}
