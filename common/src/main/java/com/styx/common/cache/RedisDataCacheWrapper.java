package com.styx.common.cache;

/**
 * @author TontoZhou
 * @since 2021/3/19
 */
public class RedisDataCacheWrapper<T> extends DataCacheWrapper<T> {

    private RedisDataCacheManager redisDataCacheManager;
    private long version;
    private String cacheId;

    public RedisDataCacheWrapper(DataCache<T> dataCache, RedisDataCacheManager redisDataCacheManager) {
        super(dataCache);
        this.cacheId = dataCache.getId();
        this.redisDataCacheManager = redisDataCacheManager;
    }


    public void toLoad() {
        redisDataCacheManager.incrementDataVersion(cacheId);
    }

    public T getData() {
        synchronized (source) {
            if (!loaded) {
                data = source.loadData();
                loaded = true;
            }
            return data;
        }
    }

}
