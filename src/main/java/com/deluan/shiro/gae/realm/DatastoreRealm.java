package com.deluan.shiro.gae.realm;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/**
 * @author deluan
 * @author hleinone
 */
public class DatastoreRealm extends AuthorizingRealm {
	public static final String DEFAULT_USER_STORE_KIND = "ShiroUsers";

	private static final Logger logger = LoggerFactory
			.getLogger(DatastoreRealm.class);
	private final DatastoreService datastoreService;
	private String userStoreKind;

	public DatastoreRealm() {
		this(DEFAULT_USER_STORE_KIND);
	}

	public DatastoreRealm(String userStoreKind) {
		this(DatastoreServiceFactory.getDatastoreService(), userStoreKind);
	}

	private DatastoreRealm(DatastoreService datastoreService,
			String userStoreKind) {
		logger.info("Creating a new instance of DatastoreRealm");
		this.datastoreService = datastoreService;
		this.userStoreKind = userStoreKind;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		final String username = ((UsernamePasswordToken) token).getUsername();
		logger.info("Attempting to authenticate " + username
				+ " in DB realm...");

		// Null username is invalid
		if (username == null)
			throw new AccountException(
					"Null usernames are not allowed by this realm.");

		// Get the user with the given username. If the user is not
		// found, then they don't have an account and we throw an
		// exception.
		final Entity user = findByUsername(username);
		if (user == null)
			throw new UnknownAccountException("No account found for user '"
					+ username + "'");

		logger.info("Found user " + username + " in DB");

		return new SimpleAccount(username, user.getProperty("passwordHash"),
				"DatastoreRealm");
	}

	private Entity findByUsername(String username) {
		final Query query = new Query(userStoreKind).addFilter("username", Query.FilterOperator.EQUAL, username);
		final PreparedQuery preparedQuery = datastoreService.prepare(query);
		return preparedQuery.asSingleEntity();
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		return null; // TODO
	}

    public void setUserStoreKind(String userStoreKind) {
        this.userStoreKind = userStoreKind;
    }
}