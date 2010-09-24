package com.deluan.shiro.gae.cache;

import com.google.appengine.api.memcache.MemcacheService;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Set;

public class Memcache<K, V> implements Cache<K, V> {

    private MemcacheService memcacheService;

    Memcache(MemcacheService memcacheService) {
        this.memcacheService = memcacheService;
    }

    public V get(K k) throws CacheException {
        return (V) memcacheService.get(k);
    }

    public V put(K k, V v) throws CacheException {
        V oldValue = get(k);
        memcacheService.put(k, v);
        return oldValue;
    }

    public V remove(K k) throws CacheException {
        V oldValue = get(k);
        memcacheService.delete(k);
        return oldValue;
    }

    public void clear() throws CacheException {
        memcacheService.clearAll();
    }

    public int size() {
        return (int) memcacheService.getStatistics().getItemCount();
    }

    public Set<K> keys() {
        // FIXME How to implement this?! Is it really necessary?
        return null;
    }

    public Collection<V> values() {
        // FIXME How to implement this?! Is it really necessary?
        return null;
    }
}
