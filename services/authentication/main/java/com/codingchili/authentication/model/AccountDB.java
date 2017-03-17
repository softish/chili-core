package com.codingchili.authentication.model;

import io.vertx.core.*;

import com.codingchili.core.context.CoreContext;
import com.codingchili.core.security.Account;
import com.codingchili.core.security.HashHelper;
import com.codingchili.core.storage.AsyncStorage;
import com.codingchili.core.storage.exception.ValueAlreadyPresentException;
import com.codingchili.core.storage.exception.ValueMissingException;

import static io.vertx.core.Future.*;

/**
 * @author Robin Duda
 *         <p>
 *         Account storage logic.
 */
public class AccountDB implements AsyncAccountStore {
    private final AsyncStorage<AccountMapping> accounts;
    private final HashHelper hasher;

    public AccountDB(AsyncStorage<AccountMapping> map, CoreContext context) {
        this.accounts = map;
        this.hasher = new HashHelper(context);
    }

    @Override
    public void get(Handler<AsyncResult<Account>> future, String username) {
        accounts.get(username, user -> {
            if (user.succeeded()) {
                future.handle(succeededFuture(filter(user.result())));
            } else {
                future.handle(failedFuture(user.cause()));
            }
        });
    }

    @Override
    public void authenticate(Handler<AsyncResult<Account>> future, Account account) {
        accounts.get(account.getUsername(), map -> {
            if (map.succeeded()) {
                authenticate(future, map.result(), account);
            } else {
                fail(future, map);
            }
        });
    }

    @Override
    public void register(Handler<AsyncResult<Account>> future, Account account) {
        AccountMapping mapping = new AccountMapping(account);

        hasher.hash(hash -> {
            mapping.setHash(hash.result());

            accounts.putIfAbsent(mapping, map -> {
                if (map.succeeded()) {
                    future.handle(Future.succeededFuture(filter(account)));
                } else {
                    fail(future, map);
                }
            });
        }, account.getCharPassword());
    }

    private void fail(Handler<AsyncResult<Account>> future, AsyncResult result) {
        Throwable cause = result.cause();

        if (cause instanceof ValueAlreadyPresentException) {
            future.handle(failedFuture(new AccountExistsException()));
        } else if (cause instanceof ValueMissingException) {
            future.handle(failedFuture(new AccountMissingException()));
        } else {
            future.handle(failedFuture(cause));
        }
    }

    private void authenticate(Handler<AsyncResult<Account>> future, AccountMapping authenticated, Account unauthenticated) {
        hasher.verify(verify -> {
            if (verify.succeeded()) {
                future.handle(succeededFuture(filter(authenticated)));
            } else {
                future.handle(failedFuture(new AccountPasswordException()));
            }
        }, authenticated.getHash(), unauthenticated.getCharPassword());
    }

    private Account filter(AccountMapping account) {
        return new Account()
                .setEmail(account.getEmail())
                .setUsername(account.getUsername());
    }

    private Account filter(Account account) {
        hasher.wipe(account.getCharPassword());
        return account;
    }
}