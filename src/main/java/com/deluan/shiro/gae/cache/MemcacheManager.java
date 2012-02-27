package com.deluan.shiro.gae.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * @author deluan
 * @author hleinone
 */
public class MemcacheManager implements CacheManager {
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		return new Memcache<K, V>(
				MemcacheServiceFactory.getMemcacheService(name));
	}
}
