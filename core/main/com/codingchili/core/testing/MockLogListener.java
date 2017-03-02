package com.codingchili.core.testing;

/**
 * Logger mock interface for testing the logging system.
 *
 * @author Robin Duda
 */
@FunctionalInterface
public interface MockLogListener {

    /**
     * Receives the data that was logged as a {@link String}.
     * @param logged the data that was written to the logger.
     */
    void onLogged(String logged);
}
