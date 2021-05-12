package com.styx.monitor.service.message;

import com.styx.common.config.RedisConstants;
import com.styx.monitor.service.message.vo.TerminalMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author TontoZhou
 * @since 2021/5/12
 */
@Slf4j
@Service
public class TerminalMessageService {

    // 30XX年时间
    private final static long max_time = 33177712975000L;

    @Autowired
    private StringRedisTemplate redisTemplate;



}
