package com.codingchili.core.testing;

import com.codingchili.core.logging.*;

/**
 * @author Robin Duda
 *
 * Test class to mock the logger.
 */
public class LoggerMock extends ConsoleLogger {
    private MockLogListener listener;

    /**
     * Create a new logger mock.
     * @param listener a listener that receives all the event sourced
     *                 to the logger.
     */
    public LoggerMock(MockLogListener listener) {
        this.listener = listener;
    }

    @Override
    public Logger log(String line) {
        listener.onLogged(line);
        return this;
    }

    @Override
    public Logger log(String line, Level level) {
        listener.onLogged(line);
        return this;
    }
}