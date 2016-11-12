package com.codingchili.core.Protocol;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codingchili.core.Exception.AuthorizationRequiredException;
import com.codingchili.core.Exception.HandlerMissingException;
import com.codingchili.core.Testing.EmptyRequest;

import static com.codingchili.core.Protocol.Access.*;

/**
 * @author Robin Duda
 */
@RunWith(VertxUnitRunner.class)
public class ProtocolTest {
    private static final String TEST = "test";
    private static final String ANOTHER = "another";
    private Protocol<RequestHandler<Request>> protocol;

    @Before
    public void setUp() {
        protocol = new Protocol<>();
    }

    @Test
    public void testHandlerMissing(TestContext test) throws Exception {
        Async async = test.async();

        try {
            protocol.use(TEST, Request::accept, PUBLIC)
                    .get(PUBLIC, ANOTHER).handle(new EmptyRequest());

            test.fail("Should throw handler missing exception.");
        } catch (HandlerMissingException e) {
            async.complete();
        }
    }

    @Test
    public void testPrivateRouteNoAccess(TestContext test) throws Exception {
        Async async = test.async();

        try {
            protocol.use(TEST, Request::accept, AUTHORIZED)
                    .get(PUBLIC, TEST).handle(new EmptyRequest());

            test.fail("Should throw authorization exception.");
        } catch (AuthorizationRequiredException e) {
            async.complete();
        }
    }

    @Test
    public void testPublicRouteWithAccess(TestContext test) throws Exception {
        Async async = test.async();

        protocol.use(TEST, Request::accept, PUBLIC)
                .get(PUBLIC, TEST).handle(new EmptyRequest() {
            @Override
            public void accept() {
                async.complete();
            }
        });
    }

    @Test
    public void testPrivateRoute(TestContext test) throws Exception {
        Async async = test.async();

        protocol.use(TEST, Request::accept, AUTHORIZED)
                .get(AUTHORIZED, TEST).handle(new EmptyRequest() {
            @Override
            public void accept() {
                async.complete();
            }
        });
    }

    @Test
    public void testPublicRoute(TestContext test) throws Exception {
        Async async = test.async();

        protocol.use(TEST, Request::accept, PUBLIC)
                .get(PUBLIC, TEST).handle(new EmptyRequest() {
            @Override
            public void accept() {
                async.complete();
            }
        });
    }

    @Test
    public void testListRoutes(TestContext test) {
        protocol.use(TEST, Request::accept, PUBLIC)
                .use(ANOTHER, Request::accept, AUTHORIZED);

        ProtocolMapping mapping = protocol.list();

        test.assertEquals(2, mapping.getRoutes().size());
        test.assertEquals(AUTHORIZED, mapping.getRoutes().get(0).getAccess());
        test.assertEquals(ANOTHER, mapping.getRoutes().get(0).getRoute());
        test.assertEquals(PUBLIC, mapping.getRoutes().get(1).getAccess());
        test.assertEquals(TEST, mapping.getRoutes().get(1).getRoute());
    }
}
