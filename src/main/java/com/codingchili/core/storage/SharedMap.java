package com.codingchili.core.storage;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;

import java.util.List;

import com.codingchili.core.context.*;
import com.codingchili.core.storage.exception.*;

import static com.codingchili.core.context.FutureHelper.error;
import static com.codingchili.core.context.FutureHelper.result;

/**
 * @author Robin Duda
 *
 * Storage implementation that uses vertx local-shared map.
 */
public class SharedMap<Key, Value> implements AsyncStorage<Key, Value> {
    private CoreContext context;
    private LocalMap<Key, Value> map;

    public SharedMap(Future<AsyncStorage<Key, Value>> future, StorageContext<Value> context) {
        this.context = context;
        this.map = context.vertx().sharedData().getLocalMap(context.DB() + "." + context.collection());
        future.complete(this);
    }

    @Override
    public void get(Key key, Handler<AsyncResult<Value>> handler) {
        Value value = map.get(key);

        if (value != null) {
            handler.handle(result(value));
        } else {
            handler.handle(error(new ValueMissingException(key)));
        }
    }

    @Override
    public void put(Key key, Value value, Handler<AsyncResult<Void>> handler) {
        map.put(key, value);
        handler.handle(FutureHelper.result());
    }

    @Override
    public void put(Key key, Value value, long ttl, Handler<AsyncResult<Void>> handler) {
        put(key, value, handler);
        context.timer(ttl, event -> remove(key, (removed) -> {}));
    }

    @Override
    public void putIfAbsent(Key key, Value value, Handler<AsyncResult<Void>> handler) {
        if (map.putIfAbsent(key, value) == null) {
            handler.handle(FutureHelper.result());
        } else {
            handler.handle(error(new ValueAlreadyPresentException(key)));
        }
    }

    @Override
    public void putIfAbsent(Key key, Value value, long ttl, Handler<AsyncResult<Void>> handler) {
        putIfAbsent(key, value, handler);
        context.timer(ttl, event -> remove(key, (removed) -> {}));
    }

    @Override
    public void remove(Key key, Handler<AsyncResult<Void>> handler) {
        Value value = map.remove(key);

        if (value == null) {
            handler.handle(error(new NothingToRemoveException(key)));
        } else {
            handler.handle(FutureHelper.result());
        }
    }

    @Override
    public void replace(Key key, Value value, Handler<AsyncResult<Void>> handler) {
        if (map.replace(key, value) != null) {
            handler.handle(FutureHelper.result());
        } else {
            handler.handle(error(new NothingToReplaceException(key)));
        }
    }

    @Override
    public void clear(Handler<AsyncResult<Void>> handler) {
        map.clear();
        handler.handle(FutureHelper.result());
    }

    @Override
    public void size(Handler<AsyncResult<Integer>> handler) {
        handler.handle(result(map.size()));
    }

    @Override
    public void queryExact(JsonObject attributes, Handler<AsyncResult<List<Value>>> handler) {

    }

    @Override
    public void querySimilar(JsonObject attributes, Handler<AsyncResult<List<Value>>> handler) {

    }

    @Override
    public void queryRange(int from, int to, Handler<AsyncResult<List<Value>>> handler, String... attributes) {

    }
}