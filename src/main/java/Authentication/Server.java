package Authentication;

import Authentication.Controller.ClientHandler;
import Authentication.Controller.Transport.ClientServer;
import Authentication.Controller.Transport.RealmServer;
import Authentication.Configuration.AuthProvider;
import Authentication.Controller.RealmHandler;
import Logging.Model.Logger;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

/**
 * @author Robin Duda
 *         Starts up the client handler and the realmName handler.
 */
public class Server implements Verticle {
    private AuthProvider provider;
    private Vertx vertx;
    private Logger logger;

    public Server() {
    }

    public Server(AuthProvider store) {
        this.provider = store;
        this.logger = store.getLogger();
    }

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;

        if (provider == null) {
            this.provider = new AuthProvider(vertx);
            this.logger = provider.getLogger();
        }

        monitorDatabase();
    }

    /**
     * Monitor the database connection and log connection issues.
     */
    private void monitorDatabase() {
        vertx.setPeriodic(provider.getDatabase().getPollRate(), (id) -> {
            Future<Void> future = Future.future();

            future.setHandler(result -> {
                if (result.failed()) {
                    logger.onDatabaseError();
                }
            });

            provider.getAccountStore().isConnected(future);
        });
    }

    @Override
    public void start(Future<Void> start) throws Exception {
        new ClientHandler(provider);
        new RealmHandler(provider);

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            vertx.deployVerticle(new ClientServer(provider));
            vertx.deployVerticle(new RealmServer(provider));
        }

        logger.onServerStarted(start);
    }


    @Override
    public void stop(Future<Void> stop) throws Exception {
        logger.onServerStopped(stop);
    }
}
