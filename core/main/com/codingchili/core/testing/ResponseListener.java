package com.codingchili.core.testing;

import io.vertx.core.json.JsonObject;

import com.codingchili.core.protocol.ResponseStatus;

/**
 * An interface used to listen for a response from a service.
 * Instead of invoking {@link com.codingchili.core.protocol.Request#write(Object)}
 * the handle method is invoked with the object formatted as Json, and the
 * status of the message.
 *
 * @author Robin Duda
 */

@FunctionalInterface
public interface ResponseListener {
    /**
     * Receive the response from a called service.
     * @param response the response data
     * @param status the status of the received response
     */
    void handle(JsonObject response, ResponseStatus status);
}

