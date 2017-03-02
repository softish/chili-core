package com.codingchili.core.testing;

import io.vertx.core.Vertx;

import com.codingchili.core.configuration.ServiceConfigurable;
import com.codingchili.core.context.*;
import com.codingchili.core.security.RemoteIdentity;

/**
 * Mock context for testing.
 *
 * @author Robin Duda
 */
public class ContextMock extends ServiceContext implements CoreContext {
    public ContextMock(Vertx vertx) {
        super(vertx);
    }

    public ContextMock(CoreContext context) {
        super(context);
    }

    @Override
    protected ServiceConfigurable service() {
        return new ServiceConfigurable() { };
    }

    @Override
    public RemoteIdentity identity() {
        return new RemoteIdentity("mock.node", "localhost");
    }
}
