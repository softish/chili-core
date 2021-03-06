package com.codingchili.authentication.configuration;

import com.codingchili.authentication.model.AccountDB;
import com.codingchili.authentication.model.AccountMapping;
import com.codingchili.authentication.model.AsyncAccountStore;
import com.codingchili.core.context.CoreContext;
import com.codingchili.core.context.ServiceContext;
import com.codingchili.core.files.Configurations;
import com.codingchili.core.logging.Level;
import com.codingchili.core.security.Account;
import com.codingchili.core.security.Token;
import com.codingchili.core.security.TokenFactory;
import com.codingchili.core.storage.StorageLoader;
import io.vertx.core.Future;

import static com.codingchili.authentication.configuration.AuthenticationSettings.PATH_AUTHSERVER;
import static com.codingchili.common.Strings.*;

/**
 * @author Robin Duda
 * <p>
 * Authentication service context.
 */
public class AuthenticationContext extends ServiceContext {
    private AsyncAccountStore accounts;

    public AuthenticationContext(CoreContext core) {
        super(core);
    }

    public static void create(Future<AuthenticationContext> future, CoreContext core) {
        AuthenticationContext context = new AuthenticationContext(core);
        new StorageLoader<AccountMapping>(context)
                .withPlugin(context.service().getStorage())
                .withCollection(COLLECTION_ACCOUNTS)
                .withClass(AccountMapping.class)
                .build(prepare -> {
                    context.accounts = new AccountDB(prepare.result(), context);
                    future.complete(context);
                });
    }

    public AsyncAccountStore getAccountStore() {
        return accounts;
    }

    public boolean verifyClientToken(Token token) {
        return new TokenFactory(service().getClientSecret()).verifyToken(token);
    }

    public Token signClientToken(String domain) {
        return new Token(new TokenFactory(service().getClientSecret()), domain);
    }

    public AuthenticationSettings service() {
        return Configurations.get(PATH_AUTHSERVER, AuthenticationSettings.class);
    }

    public void onAuthenticationFailure(Account account, String host) {
        log(event(LOG_ACCOUNT_UNAUTHORIZED, Level.WARNING)
                .put(LOG_USER, account.getUsername())
                .put(LOG_REMOTE, host));
    }

    public void onAuthenticated(String username, String host) {
        log(event(LOG_ACCOUNT_AUTHENTICATED)
                .put(LOG_USER, username)
                .put(LOG_REMOTE, host));
    }

    public void onRegistered(String username, String host) {
        log(event(LOG_ACCOUNT_REGISTERED)
                .put(LOG_USER, username)
                .put(LOG_REMOTE, host));
    }
}