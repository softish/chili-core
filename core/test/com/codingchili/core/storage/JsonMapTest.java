package com.codingchili.core.storage;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.codingchili.core.testing.MapTestCases;

/**
 * @author Robin Duda
 *         <p>
 *         Tests for the storage providers in core. Reuse these tests when new
 *         storage subsystems are implemented using the StorageLoader.
 */
@RunWith(VertxUnitRunner.class)
public class JsonMapTest extends MapTestCases {
    @Before
    public void setUp(TestContext test) {
        super.setUp(test.async(), JsonMap.class, Vertx.vertx());
    }
}
