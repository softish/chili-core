package com.codingchili.realmregistry.model;

import com.codingchili.realmregistry.configuration.RealmSettings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.codingchili.core.security.Token;
import com.codingchili.core.security.TokenFactory;
import com.codingchili.core.storage.*;

import static com.codingchili.core.configuration.CoreStrings.*;
import static io.vertx.core.Future.*;


/**
 * @author Robin Duda
 *         <p>
 *         Shares realmName data between the clienthandler and the realmhandler.
 *         Allows the deployment of multiple handlers.
 */
public class RealmDB implements AsyncRealmStore {
    private final AsyncStorage<RealmSettings> realms;
    private EntryWatcher<RealmSettings> watcher;
    private int timeout = 15000;

    public RealmDB(AsyncStorage<RealmSettings> map) {
        this.realms = map;

        this.watcher = getStaleQuery().poll(stale -> {
            realms.remove(stale.id(), (removed) -> {});
        }, this::getTimeout);
    }

    private QueryBuilder<RealmSettings> getStaleQuery() {
        return realms.query(ID_MODIFIED).between(0L, getLastValidTime());
    }

    private long getLastValidTime() {
        return Instant.now().toEpochMilli() - timeout;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public RealmDB setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public void getMetadataList(Handler<AsyncResult<List<RealmMetaData>>> future) {
        realms.query(ID_NAME).like("").execute(map -> {
            if (map.succeeded()) {
                List<RealmMetaData> list = map.result().stream()
                        .map(RealmMetaData::new)
                        .collect(Collectors.toList());

                future.handle(succeededFuture((list)));
            } else {
                future.handle(failedFuture(map.cause()));
            }
        });
    }

    @Override
    public void signToken(Handler<AsyncResult<Token>> future, String realm, String domain) {
        realms.get(realm, map -> {
            if (map.succeeded()) {
                RealmSettings settings = map.result();
                future.handle(succeededFuture(new Token(new TokenFactory(getSecretBytes(settings)), domain)));
            } else {
                future.handle(failedFuture(map.cause()));
            }
        });
    }

    private byte[] getSecretBytes(RealmSettings settings) {
        return settings.getAuthentication().getKey().getBytes();
    }

    @Override
    public void get(Handler<AsyncResult<RealmSettings>> future, String realmName) {
        realms.get(realmName, map -> {
            if (map.succeeded()) {
                future.handle(succeededFuture(map.result()));
            } else {
                future.handle(failedFuture(map.cause()));
            }
        });
    }

    @Override
    public void put(Handler<AsyncResult<Void>> future, RealmSettings realm) {
        realms.put(realm, future);
    }

    @Override
    public void remove(Handler<AsyncResult<Void>> future, String realmName) {
        realms.remove(realmName, future);
    }
}

