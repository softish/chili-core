package com.codingchili.authentication.controller;

import io.vertx.core.Future;

import com.codingchili.core.context.CoreException;
import com.codingchili.core.protocol.*;
import com.codingchili.core.security.Account;

import com.codingchili.authentication.configuration.AuthenticationContext;
import com.codingchili.authentication.model.*;

import static com.codingchili.core.protocol.Access.PUBLIC;
import static com.codingchili.common.Strings.*;

/**
 * @author Robin Duda
 *         Routing used to authenticate users and create/delete characters.
 */
public class ClientHandler<T extends AuthenticationContext> extends AbstractHandler<T> {
    private final Protocol<RequestHandler<ClientRequest>> protocol = new Protocol<>();
    private final AsyncAccountStore accounts;

    public ClientHandler(T context) {
        super(context, NODE_AUTHENTICATION_CLIENTS);

        accounts = context.getAccountStore();

        protocol.use(CLIENT_REGISTER, this::register, PUBLIC)
                .use(CLIENT_AUTHENTICATE, this::authenticate, PUBLIC)
                .use(ID_PING, Request::accept, PUBLIC);
    }

    private Access authenticate(Request request) {
        boolean authorized = context.verifyClientToken(request.token());
        return (authorized) ? Access.AUTHORIZED : PUBLIC;
    }

    @Override
    public void handle(Request request) throws CoreException {
        protocol.get(authenticate(request), request.route()).handle(new ClientRequest(request));
    }

    private void register(ClientRequest request) {
        Future<Account> future = Future.future();

        future.setHandler(result -> {
            try {
                if (future.succeeded()) {
                    sendAuthentication(result.result(), request, true);
                } else {
                    throw future.cause();
                }
            } catch (AccountExistsException e) {
                request.conflict(e);
            } catch (Throwable e) {
                request.error(e);
            }
        });
        accounts.register(future, request.getAccount());
    }

    private void authenticate(ClientRequest request) {
        Future<Account> future = Future.future();

        future.setHandler(result -> {
            try {
                if (future.succeeded()) {
                    sendAuthentication(result.result(), request, false);
                } else
                    throw future.cause();

            } catch (AccountMissingException e) {
                request.missing(e);
            } catch (AccountPasswordException e) {
                context.onAuthenticationFailure(request.getAccount().getUsername(), request.sender());
                request.unauthorized(e);
            } catch (Throwable e) {
                request.error(e);
            }
        });
        accounts.authenticate(future, request.getAccount());
    }

    private void sendAuthentication(Account account, ClientRequest request, boolean registered) {
        request.write(
                new ClientAuthentication(
                        account,
                        context.signClientToken(account.getUsername()),
                        registered));

        if (registered)
            context.onRegistered(account.getUsername(), request.sender());
        else
            context.onAuthenticated(account.getUsername(), request.sender());
    }
}