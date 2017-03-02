package com.codingchili.core.testing;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import com.codingchili.core.context.StorageContext;
import com.codingchili.core.files.Configurations;
import com.codingchili.core.storage.*;


/**
 * Common test cases for the map implementations.
 *
 * @author Robin Duda
 */
@Ignore("Extend this class to run the tests.")
@RunWith(VertxUnitRunner.class)
public class MapTestCases {
    private static final String LEVEL = "level";
    private static final String ID = "id";
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    private static final String KEY_MISSING = "KEY_MISSING";
    private static final StorageObject OBJECT_ONE = new StorageObject("one", 1);
    private static final StorageObject OBJECT_TWO = new StorageObject("two", 2);
    private static final StorageObject OBJECT_THREE = new StorageObject("three", 3);
    private static final String DB_NAME = "spinach";
    private static final String COLLECTION = "leaves";
    private static final int PREPARED_ITEM_COUNT = 2;
    protected StorageContext<StorageObject> context;
    protected AsyncStorage<String, StorageObject> store;
    protected static Integer delay = 1;

    @Rule
    public Timeout timeout = Timeout.seconds(6);

    /**
     * Test setup method that must be called by extending class.
     * @param async a test async method to support asynchronous initialization of
     *              the given storage.
     * @param plugin the plugin class that is to be loaded and tested.
     * @param vertx a vertx instance, this will be closed in the tearDown method.
     *              If clustering is required the Vertx instance provided must be
     *              clustered.
     */
    @Before
    public void setUp(Async async, Class plugin, Vertx vertx) {
        Future<AsyncStorage<String, StorageObject>> future = Future.future();

        context = new StorageContext<>(vertx);

        future.setHandler(map -> {
            store = map.result();
            prepareStore(async);
        });

        StorageLoader.prepare()
                .withDB(DB_NAME)
                .withCollection(COLLECTION)
                .withClass(StorageObject.class)
                .withContext(context)
                .withPlugin(plugin)
                .build(future);
    }

    private void prepareStore(Async async) {
        store.clear(result -> {
            store.put(TWO, OBJECT_TWO, object -> {
                store.put(THREE, OBJECT_THREE, other -> {
                    context.timer(delay, event -> {
                        async.complete();
                    });
                });
            });
        });
    }

    @After
    public void tearDown(TestContext test) {
        context.vertx().close(test.asyncAssertSuccess());
    }

    /**
     * Test that the get method can retrieve an object by its id.
     */
    @Test
    public void testGet(TestContext test) {
        Async async = test.async();

        store.get(TWO, get -> {
            test.assertTrue(get.succeeded());
            test.assertEquals(OBJECT_TWO, get.result());
            async.complete();
        });
    }

    /**
     * tests that get on a missing id fails with {@link ValueMissingException}.
     */
    @Test
    public void testGetMissing(TestContext test) {
        Async async = test.async();

        store.get(KEY_MISSING, get -> {
            test.assertTrue(get.failed());
            test.assertNull(get.result());
            test.assertEquals(ValueMissingException.class, get.cause().getClass());
            async.complete();
        });
    }

    /**
     * Test that the put operation adds an item to the storage.
     * The operation is verified by using the get method to retrieve it.
     */
    @Test
    public void testPut(TestContext test) {
        Async async = test.async();

        store.put(ONE, OBJECT_ONE, put -> {
            test.assertTrue(put.succeeded());

            store.get(ONE, get -> {
                test.assertEquals(OBJECT_ONE, get.result());
                test.assertTrue(get.succeeded());
                async.complete();
            });
        });
    }

    /**
     * Test the put method with a given TTL, waits for the TTL to expire
     * and then uses the get method to assert that the item is removed.
     */
    @Test
    public void testPutWithTTL(TestContext test) {
        Async async = test.async();

        store.put(ONE, OBJECT_ONE, 50, put -> {
            test.assertTrue(put.succeeded());
            waitForExpiry(test, async);
        });
    }

    private void waitForExpiry(TestContext test, Async async) {
        context.timer(800, event -> store.get(ONE, get -> {
            test.assertTrue(get.failed());
            async.complete();
        }));
    }

    /**
     * Tests that an object is only added to the storage if it
     * does not exist, meaning it won't update an existing entry.
     * The operation is verified using the get method.
     */
    @Test
    public void testPutIfAbsent(TestContext test) {
        Async async = test.async();

        store.putIfAbsent(ONE, OBJECT_ONE, put -> {
            test.assertTrue(put.succeeded());

            store.get(ONE, get -> {
                test.assertTrue(get.succeeded());
                async.complete();
            });
        });
    }

    /**
     * Tests putting an absent item with a TTL and then verifying
     * that it has been removed after the TTL has expired.
     */
    @Test
    public void testPutIfAbsentTTL(TestContext test) {
        Async async = test.async();

        store.putIfAbsent(ONE, OBJECT_ONE, 50, put -> {
            test.assertTrue(put.succeeded());
            waitForExpiry(test, async);
        });
    }

    /**
     * Tests putting an item if not absent, when the item is already present.
     * Verifies that the put operation fails by expecting an exception of type
     * {@link ValueAlreadyPresentException}
     */
    @Test
    public void testPutIfAbsentNotAbsent(TestContext test) {
        Async async = test.async();

        store.putIfAbsent(TWO, OBJECT_TWO, inner -> {
            test.assertTrue(inner.failed());
            test.assertEquals(ValueAlreadyPresentException.class, inner.cause().getClass());
            async.complete();
        });
    }

    /**
     * Tests removing an item that is present in the storage.
     * Verifies that it has been removed by using the get operation.
     */
    @Test
    public void testRemove(TestContext test) {
        Async async = test.async();

        store.remove(TWO, inner -> {
            test.assertTrue(inner.succeeded());

            store.get(TWO, result -> {
                test.assertTrue(result.failed());
                async.complete();
            });
        });
    }

    /**
     * Tests removing an item from the storage that is not present.
     * Verifies that the item was not deleted by expecting an error of type
     * {@link NothingToRemoveException}
     */
    @Test
    public void testRemoveNotPresent(TestContext test) {
        Async async = test.async();

        store.remove(KEY_MISSING, remove -> {
            test.assertTrue(remove.failed());
            test.assertEquals(NothingToRemoveException.class, remove.cause().getClass());
            async.complete();
        });
    }

    /**
     * Tests replacing an item that is present with another item.
     * Verifies that the item was replaced by using the get operation.
     */
    @Test
    public void testReplace(TestContext test) {
        Async async = test.async();

        store.replace(TWO, OBJECT_ONE, replace -> {
            test.assertTrue(replace.succeeded());

            store.get(TWO, get -> {
                test.assertEquals(OBJECT_ONE, get.result());
                test.assertTrue(get.succeeded());
                async.complete();
            });
        });
    }

    /**
     * Tests replacing and item that is not present.
     * Verifies that it is not possible by expecting an exception of type
     * {@link NothingToReplaceException}
     */
    @Test
    public void testReplaceIfNotPresent(TestContext test) {
        Async async = test.async();

        store.replace(KEY_MISSING, OBJECT_ONE, replace -> {
            test.assertTrue(replace.failed());
            test.assertEquals(NothingToReplaceException.class, replace.cause().getClass());
            async.complete();
        });
    }

    /**
     * Tests clearing all items in a storage.
     * Verifies that the storage was cleared by the size method.
     */
    @Test
    public void testClear(TestContext test) {
        Async async = test.async();

        store.clear(clear -> {
            test.assertTrue(clear.succeeded());

            store.size(size -> {
                test.assertEquals(0, size.result());
                test.assertTrue(size.succeeded());
                async.complete();
            });
        });
    }

    /**
     * Tests that the size method returns the number of items in the storage.
     * Verifies that the number of items corresponds to the number of items
     * that the store was prepared with.
     */
    @Test
    public void testSize(TestContext test) {
        Async async = test.async();

        store.size(size -> {
            test.assertEquals(PREPARED_ITEM_COUNT, size.result());
            test.assertTrue(size.succeeded());
            async.complete();
        });
    }

    /**
     * Queries the storage for an exact match. Asserts that the the query was
     * successful by checking the number of returned items is 1 and the result has
     * the id that was queried for.
     */
    @Test
    public void testQueryExact(TestContext test) {
        Async async = test.async();

        store.queryExact(ID, TWO, query -> {
            test.assertTrue(query.succeeded());
            test.assertEquals(1, query.result().size());
            test.assertEquals(TWO, query.result().iterator().next().getId());
            async.complete();
        });
    }

    /**
     * Asserts that an exact query for an object that does not exist
     * returns an empty result-set.
     */
    @Test
    public void testQueryMatchNone(TestContext test) {
        Async async = test.async();

        store.queryExact(ID, ONE, query -> {
            test.assertTrue(query.succeeded());
            test.assertEquals(0, query.result().size());
            async.complete();
        });
    }

    /**
     * Tests the like query, checks that results returned contains the
     * search parameter. Verifies by checking that there is one result that
     * has the given id.
     */
    @Test
    public void testQuerySimilar(TestContext test) {
        Async async = test.async();

        store.querySimilar(ID, "thr", query -> {
            test.assertTrue(query.succeeded());
            test.assertEquals(THREE, query.result().iterator().next().getId());
            test.assertEquals(1, query.result().size());


            store.querySimilar(ID, "rht", inner -> {
                test.assertTrue(inner.succeeded());
                test.assertEquals(0, inner.result().size());
                async.complete();
            });
        });
    }

    /**
     * Tests that query parameters for the like operation requires at least
     * the configured number of feedback chars before returning results.
     */
    @Test
    public void testQuerySimilarTooShortExpression(TestContext test) {
        Async async = test.async();

        // don't test if set to 2 or less, default is 3.
        if (Configurations.storage().getMinFeedbackChars() > 2) {
            store.querySimilar(ID, "th", query -> {
                test.assertTrue(query.succeeded());
                test.assertEquals(0, query.result().size());
                async.complete();
            });
        } else {
            async.complete();
        }
    }

    /**
     * Tests a like query where the given query parameter contains invalid chars.
     * Invalid chars are any characters not considered plaintext by the
     * {@link com.codingchili.core.security.Validator}. This is used to prevent
     * processing user input that contains high-complexity regular expressions.
     */
    @Test
    public void testQuerySimilarInvalidExpression(TestContext test) {
        Async async = test.async();

        store.querySimilar(ID, "val.*", query -> {
            test.assertTrue(query.succeeded());
            test.assertEquals(0, query.result().size());
            async.complete();
        });
    }

    /**
     * Tests the range query and verifies by checking the range of
     * the returned items.
     */
    @Test
    public void testQueryRange(TestContext test) {
        Async async = test.async();

        store.queryRange(LEVEL, 2, 3, query -> {
            test.assertTrue(query.succeeded());
            test.assertEquals(2, query.result().size());

            query.result().forEach(item -> {
                test.assertTrue(item.getLevel() == 2 || item.getLevel() == 3);
            });

            async.complete();
        });
    }

    /**
     * Tests a range query that does not return any results. Verifies
     * by checking that the number of returned results is 0.
     */
    @Test
    public void testQueryRangeNoMatches(TestContext test) {
        Async async = test.async();

        store.queryRange(LEVEL, 100, 200, query -> {
            test.assertTrue(query.succeeded());
            test.assertEquals(0, query.result().size());
            async.complete();
        });
    }
}
