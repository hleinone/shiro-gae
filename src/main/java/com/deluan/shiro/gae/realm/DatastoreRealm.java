package com.deluan.shiro.gae.realm;

import com.google.appengine.api.datastore.*;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.Sha512CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.logging.Logger;

/**
 * User: deluan
 * Date: Sep 21, 2010
 * Time: 9:32:45 PM
 */
public class DatastoreRealm extends AuthorizingRealm {

    static final String DEFAULT_USER_STORE_KIND = "ShiroUsers";

    static final Logger log = Logger.getLogger("com.deluan.shiro.gae.realm.DatastoreRealm");
    private CredentialsMatcher credentialsMatcher;
    private DatastoreService datastoreService;
    private String userStoreKind = DEFAULT_USER_STORE_KIND;

    public DatastoreRealm() {
        log.info("Creating a new instance of DatastoreRealm");
        this.datastoreService = DatastoreServiceFactory.getDatastoreService();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = ((UsernamePasswordToken)token).getUsername();
        log.info("Attempting to authenticate " + username + " in DB realm...");

        // Null username is invalid
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        // Get the user with the given username. If the user is not
        // found, then they don't have an account and we throw an
        // exception.
        Entity user = findByUsername(username);
        if (user == null) {
            throw new UnknownAccountException("No account found for user '" + username + "'");
        }

        log.info("Found user " + username + " in DB");

        // Now check the user's password against the hashed value stored
        // in the database.
        SimpleAccount account = new SimpleAccount(username, user.getProperty("passwordHash"), "DatastoreRealm");
        if (!doCredentialsMatch(token, account)) {
            log.info("Invalid password (Datastore Realm)");
            throw new IncorrectCredentialsException("Invalid password for user '" + username + "'");
        }

        return account;
    }

    private Entity findByUsername(String username) {
        Query query = new Query(DEFAULT_USER_STORE_KIND);
        query.addFilter("username", Query.FilterOperator.EQUAL, username);
        PreparedQuery preparedQuery = datastoreService.prepare(query);
        return preparedQuery.asSingleEntity();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;  // TODO
    }

    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        this.credentialsMatcher = credentialsMatcher;
    }

    public void setUserStoreKind(String userStoreKind) {
        this.userStoreKind = userStoreKind;
    }

    private Boolean doCredentialsMatch(AuthenticationToken authToken, SimpleAccount account) {
        if (credentialsMatcher == null) {
            credentialsMatcher = new Sha512CredentialsMatcher();
        }
        return credentialsMatcher.doCredentialsMatch(authToken, account);
    }
}
