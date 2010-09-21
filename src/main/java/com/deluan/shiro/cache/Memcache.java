package com.deluan.shiro.cache;

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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void clear() throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int size() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<K> keys() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<V> values() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
