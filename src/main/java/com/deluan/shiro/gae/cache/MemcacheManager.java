package com.deluan.shiro.gae.cache;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

public class MemcacheManager implements CacheManager {
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService(name);

        return new Memcache<K, V>(memcacheService);
    }
}
