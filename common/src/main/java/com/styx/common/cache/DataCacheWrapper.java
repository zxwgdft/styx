package com.styx.common.cache;

/**
 * @author TontoZhou
 * @since 2021/3/19
 */
public class DataCacheWrapper<T> {

    protected DataCache<T> source;
    protected T data;
    protected boolean loaded = false;

    public DataCacheWrapper(DataCache<T> dataCache) {
        source = dataCache;
    }

    public void toLoad() {
        synchronized (source) {
            loaded = false;
        }
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
