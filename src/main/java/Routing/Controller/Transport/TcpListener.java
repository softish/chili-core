package Routing.Controller.Transport;

import Protocols.ClusterVerticle;
import Routing.Configuration.RouteProvider;
import Routing.Controller.RoutingHandler;
import Routing.Model.ListenerSettings;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * @author Robin Duda
 */
public class TcpListener extends ClusterVerticle {
    public TcpListener(RoutingHandler handler, ListenerSettings listener) {

    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {

    }
}
