package com.codingchili.core.storage;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import com.codingchili.core.testing.MapTestCases;

/**
 * @author Robin Duda
 *         <p>
 *         Tests for the storage providers in core. Reuse these tests when new
 *         storage subsystems are implemented using the StorageLoader.
 */
@RunWith(VertxUnitRunner.class)
public class ElasticMapIT extends MapTestCases {
    private static final int ELASTIC_REFRESH = 1200;

    public ElasticMapIT() {
        /*
          sets a delay between initializing the database and starting the tests.
          This is required as elasticsearch is near-real-time only.
         */
        delay = ELASTIC_REFRESH;
    }

    @Before
    public void setUp(TestContext test) {
        super.setUp(test.async(), ElasticMap.class, Vertx.vertx());
    }

    /**
     * elasticsearch is near-realtime which means that clear
     * does not return realtime results. Hence the overriden clear method.
     */
    @Override
    @Test
    public void testClear(TestContext test) {
        Async async = test.async();

        store.clear(clear -> {
            test.assertTrue(clear.succeeded());

            context.timer(ELASTIC_REFRESH, event -> store.size(size -> {
                test.assertEquals(0, size.result());
                test.assertTrue(size.succeeded());
                async.complete();
            }));
        });
    }
}
