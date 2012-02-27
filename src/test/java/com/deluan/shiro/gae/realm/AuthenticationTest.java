package com.deluan.shiro.gae.realm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class AuthenticationTest {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final CredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(Sha512Hash.ALGORITHM_NAME);

    private DatastoreRealm datastoreRealm;

    private Entity createUser(String username, String password) {
        Entity entity = new Entity(DatastoreRealm.DEFAULT_USER_STORE_KIND);
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

        Query query = new Query(DatastoreRealm.DEFAULT_USER_STORE_KIND);
        PreparedQuery preparedQuery = ds.prepare(query);
        assertEquals(3, preparedQuery.countEntities(FetchOptions.Builder.withLimit(5)));

        datastoreRealm = new DatastoreRealm();
        datastoreRealm.setCredentialsMatcher(credentialsMatcher);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testUserValid() {
        UsernamePasswordToken token = new UsernamePasswordToken("user1", "pass1");
        AuthenticationInfo info = datastoreRealm.getAuthenticationInfo(token);
        assertNotNull(info);
        assertEquals("user1", info.getPrincipals().iterator().next());
    }

    @Test(expected = org.apache.shiro.authc.IncorrectCredentialsException.class)
    public void testPasswordInvalid() {
        UsernamePasswordToken token = new UsernamePasswordToken("user1", "pass2");
        datastoreRealm.getAuthenticationInfo(token);
    }

    @Test(expected = org.apache.shiro.authc.UnknownAccountException.class)
    public void testUserNotFound() {
        UsernamePasswordToken token = new UsernamePasswordToken("inexistent", "password");
        datastoreRealm.getAuthenticationInfo(token);
    }

    @Test(expected = org.apache.shiro.authc.AccountException.class)
    public void testNullUsername() {
        UsernamePasswordToken token = new UsernamePasswordToken(null, "password");
        datastoreRealm.getAuthenticationInfo(token);
    }

    @Test(expected = org.apache.shiro.authc.AccountException.class)
    public void testEmptyUsername() {
        UsernamePasswordToken token = new UsernamePasswordToken("", "password");
        datastoreRealm.doGetAuthenticationInfo(token);
    }

}
