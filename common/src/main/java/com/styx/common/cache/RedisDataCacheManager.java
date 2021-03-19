package com.styx.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 基于redis缓存版本号的方式的数据缓存管理
 *
 * @author TontoZhou
 * @since 2021/3/19
 */
@Slf4j
public class RedisDataCacheManager extends AbstractDataCacheManager {

    private String keyPrefix = "_DCV_";
    private RedisTemplate<String, String> redisTemplate;

    public RedisDataCacheManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisDataCacheManager(String keyPrefix, RedisTemplate<String, String> redisTemplate) {
        this.keyPrefix = keyPrefix;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected DataCacheWrapper getDataCacheWrapper(DataCache source) {
        return null;
    }


    public long getDataVersion(String containerId) {
        String version = redisTemplate.opsForValue().get(keyPrefix + containerId);
        return version == null ? 0 : Long.valueOf(version);
    }

    public void incrementDataVersion(String cacheId) {
        redisTemplate.opsForValue().increment(keyPrefix + cacheId);
    }

    public void setDataVersion(String containerId, long version) {
        redisTemplate.opsForValue().set(keyPrefix + containerId, String.valueOf(version));
    }


}
