package com.codingchili.core.benchmarking;

import com.codingchili.core.context.CoreContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.List;
import java.util.Map;

/**
 * @author Robin Duda
 * <p>
 * Groups benchmarks of the same implementation together.
 */
public interface BenchmarkImplementation {

    /**
     * Prepares an implementation for testing.
     *
     * @param core   the context to use for the benchmark
     * @param future called when the setup is complete.
     */
    void initialize(CoreContext core, Handler<AsyncResult<Void>> future);

    /**
     * Called before each benchmark is executed.
     *
     * @param future callback
     */
    void next(Future<Void> future);

    /**
     * Called after the warmup phase has completed.
     *
     * @param future callback
     */
    void reset(Handler<AsyncResult<Void>> future);

    /**
     * Called after the benchmarking has completed.
     *
     * @param future callback
     */
    void shutdown(Future<Void> future);

    /**
     * Adds a new benchmark created from an operation and name.
     *
     * @param operation the operation to execute as a benchmark.
     * @param name      the name of the operation/benchmark.
     * @return fluent
     */
    BenchmarkImplementation add(BenchmarkOperation operation, String name);

    /**
     * Adds a benchmark to the implementation group.
     *
     * @param benchmark the benchmark to add.
     * @return fluent
     */
    BenchmarkImplementation add(Benchmark benchmark);

    /**
     * @return a listof benchmarks that should be executed for the given implementation.
     */
    List<Benchmark> getBenchmarks();

    /**
     * Sets the benchmarks to a list of benchmarks, may be used when creating reports.
     *
     * @param benchmarks the benchmarks to set
     * @return fluent
     */
    BenchmarkImplementation setBenchmarks(List<Benchmark> benchmarks);

    /**
     * Name of the implementation that is used within a benchmark.
     * A good choice is {@link Class#getSimpleName()}.
     *
     * @return a string that identifies the implementation tested.
     */
    String getName();

    /**
     * May be used in the reporting phase to restructure results.
     *
     * @param name the name of the implementation
     * @return fluent.
     */
    BenchmarkImplementation setName(String name);

    /**
     * Set a property of the benchmark implementation.
     *
     * @param key   the key to identify the value.
     * @param value the value to be inserted.
     * @return fluent
     */
    BenchmarkImplementation setProperty(String key, Object value);

    /**
     * get all properties added to the implementation.
     *
     * @return a map of all the properties that has been set.
     */
    Map<String, Object> getProperties();
}
