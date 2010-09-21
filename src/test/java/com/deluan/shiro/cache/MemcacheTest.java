package com.deluan.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MemcacheTest {

    private MemcacheManager cacheManager;
    private Cache<Object, Object> cache;

    @Before
    public void createCache() {
        cacheManager = new MemcacheManager();
        cache = cacheManager.getCache("teste");
    }

    @Test
    public void testGetNonexistentObject() {
        assertNull(cache.get("abc"));
    }

    @Test
    public void testGetExistentObject() {
        cache.put("123", "abc");
        assertEquals("abc", cache.get("123"));
    }
}
