package com.codingchili.core.Storage;

import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import com.codingchili.core.Testing.MapTestCases;

/**
 * @author Robin Duda
 *         <p>
 *         Tests for the storage providers in core. Reuse these tests when new
 *         storage subsystems are implemented using the StorageLoader.
 */
@Ignore
@RunWith(VertxUnitRunner.class)
public class ElasticMapIT extends MapTestCases {

    @Before
    public void setUp(TestContext test) {
        super.setUp(test, AsyncElasticMap.class);
    }

    @After
    public void tearDown(TestContext test) {
        super.tearDown(test);
    }
}