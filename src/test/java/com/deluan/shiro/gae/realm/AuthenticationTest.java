package com.deluan.shiro.gae.realm;

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuthenticationTest {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private DatastoreRealm datastoreRealm;

    private Entity createUser(String username, String password) {
        Entity entity = new Entity(DatastoreRealm.USER_STORE);
        entity.setProperty("username", username);
        entity.setProperty("passwordHash", new Sha512Hash(password).toHex());
        return entity;
    }

    @Before
    public void setUp() {
        helper.setUp();
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        ds.put(createUser("user1", "pass1"));
        ds.put(createUser("user2", "pass2"));
        ds.put(createUser("user3", "pass3"));

        Query query = new Query(DatastoreRealm.USER_STORE);
        PreparedQuery preparedQuery = ds.prepare(query);
        assertEquals(3, preparedQuery.countEntities());

        datastoreRealm = new DatastoreRealm();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testUserValid() {
        UsernamePasswordToken token = new UsernamePasswordToken("user1", "pass1");
        AuthenticationInfo info = datastoreRealm.doGetAuthenticationInfo(token);
        assertNotNull(info);
        assertEquals("user1", info.getPrincipals().iterator().next());
    }

    @Test(expected = org.apache.shiro.authc.IncorrectCredentialsException.class)
    public void testPasswordInvalid() {
        UsernamePasswordToken token = new UsernamePasswordToken("user1", "pass2");
        datastoreRealm.doGetAuthenticationInfo(token);
    }

    @Test(expected = org.apache.shiro.authc.UnknownAccountException.class)
    public void testUserNotFound() {
        UsernamePasswordToken token = new UsernamePasswordToken("inexistent", "password");
        datastoreRealm.doGetAuthenticationInfo(token);
    }

    @Test(expected = org.apache.shiro.authc.AccountException.class)
    public void testNullUsername() {
        UsernamePasswordToken token = new UsernamePasswordToken(null, "password");
        datastoreRealm.doGetAuthenticationInfo(token);
    }

    @Test(expected = org.apache.shiro.authc.AccountException.class)
    public void testEmptyUsername() {
        UsernamePasswordToken token = new UsernamePasswordToken("", "password");
        datastoreRealm.doGetAuthenticationInfo(token);
    }

}
