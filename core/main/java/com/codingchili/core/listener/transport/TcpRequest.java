package com.codingchili.core.listener.transport;

import com.codingchili.core.listener.BaseRequest;
import com.codingchili.core.listener.ListenerSettings;
import com.codingchili.core.protocol.Serializer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;

/**
 * @author Robin Duda
 * <p>
 * TCP request implementation.
 */
class TcpRequest extends BaseRequest {
    private int size;
    private Buffer buffer;
    private JsonObject data;
    private NetSocket socket;
    private ListenerSettings settings;

    TcpRequest(NetSocket socket, Buffer buffer, ListenerSettings settings) {
        this.size = buffer.length();
        this.buffer = buffer;
        this.socket = socket;
        this.settings = settings;
    }

    @Override
    public void init() {
        this.data = buffer.toJsonObject();
    }

    @Override
    public void write(Object object) {
        if (object instanceof Buffer) {
            socket.write((Buffer) object);
        } else {
            socket.write(Buffer.buffer(Serializer.pack(object)));
        }
    }

    @Override
    public JsonObject data() {
        return data;
    }

    @Override
    public int timeout() {
        return settings.getTimeout();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int maxSize() {
        return settings.getMaxRequestBytes();
    }
}
