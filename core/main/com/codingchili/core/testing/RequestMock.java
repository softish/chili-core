package com.codingchili.core.testing;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import com.codingchili.core.protocol.ClusterRequest;
import com.codingchili.core.protocol.ResponseStatus;

import static com.codingchili.core.configuration.CoreStrings.*;

/**
 * Mocked request object.
 *
 * @author Robin Duda
 */
public abstract class RequestMock {

    /**
     * Get a mocked request that will write output to the given listener.
     * @param listener a responselistener that receives the data written
     *                 to the request.
     * @return a Request Object that may be passed to a handler for testing.
     */
    public static ClusterRequestMock get(ResponseListener listener) {
        return get("", listener);
    }

    /**
     * Get a mocked request that will write output to the given listener.
     * @param route the route that the request is targeting.
     * @param listener a responselistener that receives the data written to the
     *                 request.
     * @return a Request Object that may be passed to a handler for testing.
     */
    public static ClusterRequestMock get(String route, ResponseListener listener) {
        return get(route, listener, new JsonObject());
    }

    /**
     * Get a mocket request that will write output to the given listener.
     * @param route the route that the request is targeting.
     * @param listener a responselistener that receives the data written to the
     *                 request.
     * @param json the request payload
     * @return a Request Object that may be passed to a handler for testing
     */
    public static ClusterRequestMock get(String route, ResponseListener listener, JsonObject json) {
        return new ClusterRequestMock(new MessageMock(route, listener, json));
    }

    private static class ClusterRequestMock extends ClusterRequest {
        ClusterRequestMock(MessageMock message) {
            super(message);
        }
    }

    private static class MessageMock implements Message {
        private JsonObject json;
        private ResponseListener listener;

        MessageMock(String route, ResponseListener listener, JsonObject json) {
            if (json == null) {
                this.json = new JsonObject();
            } else {
                this.json = json;
            }

            this.json.put(PROTOCOL_ROUTE, route);
            this.listener = listener;
        }

        @Override
        public Object body() {
            return json;
        }

        @Override
        public void reply(Object message) {
            JsonObject data = new JsonObject();

            if (message instanceof JsonObject) {
                data = (JsonObject) message;
            } else if (message instanceof Buffer) {
                data = ((Buffer) message).toJsonObject();
            }

            listener.handle(data, ResponseStatus.valueOf(data.getString(PROTOCOL_STATUS)));
        }


        @Override
        public String address() {
            return null;
        }

        @Override
        public MultiMap headers() {
            return null;
        }

        @Override
        public String replyAddress() {
            return null;
        }

        @Override
        public void reply(Object message, DeliveryOptions options) {

        }

        @Override
        public void fail(int failureCode, String message) {

        }

        @Override
        public void reply(Object message, DeliveryOptions options, Handler replyHandler) {

        }

        @Override
        public void reply(Object message, Handler replyHandler) {

        }
    }
}
