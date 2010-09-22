package com.deluan.shiro.gae.cache;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.apache.shiro.cache.Cache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MemcacheTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

    private Cache<Object, Object> cache;

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Before
    public void createCache() {
        MemcacheManager cacheManager = new MemcacheManager();
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

    @Test
    public void testSize() {
        assertEquals(0, cache.size());
        cache.put("123", "abc");
        assertEquals(1, cache.size());
    }

    @Test
    public void testReplace() {
        String firstValue = "abc";
        String secondValue = "def";
        cache.put("123", firstValue);
        assertEquals(firstValue, cache.put("123", secondValue));
        assertEquals(secondValue, cache.get("123"));
    }

    @Test
    public void testClear() {
        cache.put("1", "one");
        cache.put("2", "two");
        cache.put("3", "three");
        assertEquals(3, cache.size());
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    public void testRemove() {
        cache.put("1", "one");
        cache.put("2", "two");
        assertEquals(2, cache.size());
        cache.remove("1");
        assertEquals(1, cache.size());
        assertNull(cache.get("1"));
    }

}
