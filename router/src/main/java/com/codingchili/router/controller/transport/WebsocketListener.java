package com.codingchili.router.controller.transport;

import io.vertx.core.Future;

import com.codingchili.core.protocol.ClusterNode;

import com.codingchili.router.configuration.ListenerSettings;
import com.codingchili.router.controller.RouterHandler;

/**
 * @author Robin Duda
 */
public class WebsocketListener extends ClusterNode {
    public WebsocketListener(RouterHandler provider, ListenerSettings listener) {

    }

    @Override
    public void start(Future<Void> start) {
        start.complete();
    }
}
