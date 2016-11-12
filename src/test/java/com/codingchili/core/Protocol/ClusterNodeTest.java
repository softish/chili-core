package com.codingchili.core.Protocol;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.codingchili.core.Configuration.Strings.ERROR_CLUSTERING_REQUIRED;

/**
 * @author Robin Duda
 */
@RunWith(VertxUnitRunner.class)
public class ClusterNodeTest {
    private Vertx vertx;

    @After
    public void tearDown(TestContext test) {
        vertx.close(test.asyncAssertSuccess());
    }

    @Test
    public void testVertxNotClusteredError(TestContext test) {
        try {
            vertx = Vertx.vertx();
            vertx.deployVerticle(new ClusterNode() {});
        } catch (RuntimeException e) {
            test.assertEquals(ERROR_CLUSTERING_REQUIRED, e.getMessage());
        }
    }

    @Test
    public void testVertxClusteredOk(TestContext test) {
        Async async = test.async();

        Vertx.clusteredVertx(new VertxOptions(), handler -> {
            vertx = handler.result();
            handler.result().deployVerticle(new ClusterNode() {});
            async.complete();
        });
    }

}