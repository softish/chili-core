package com.codingchili.core.protocol;

import com.codingchili.core.context.CoreException;
import com.codingchili.core.protocol.exception.UnmappedException;
import com.codingchili.core.security.Token;

import static com.codingchili.core.configuration.CoreStrings.*;
import static com.codingchili.core.protocol.ResponseStatus.ACCEPTED;

/**
 * @author Robin Duda
 *
 * Base request class, extend this class for new transports.
 */
public abstract class BaseRequest implements Request {
    public static final String TARGET_UNDEFINED = "webserver.node";

    @Override
    public void accept() {
        send(ACCEPTED);
    }

    @Override
    public void error(Throwable exception) {
        if (exception instanceof CoreException) {
            send(((CoreException) exception).status(), exception);
        } else {
            send(ResponseStatus.ERROR, new UnmappedException());
        }
    }

    @Override
    public String route() {
        String route = data().getString(PROTOCOL_ROUTE);

        if (route == null) {
            route = DIR_SEPARATOR;
        }
        return route;
    }

    @Override
    public String target() {
        String target = data().getString(PROTOCOL_TARGET);

        if (target == null) {
            target = TARGET_UNDEFINED;
        }
        return target;
    }

    @Override
    public Token token() {
        if (data().containsKey(ID_TOKEN)) {
            return Serializer.unpack(data().getJsonObject(ID_TOKEN), Token.class);
        } else {
            return new Token();
        }
    }

    /**
     * sends a response to a request with an exception.
     * @param status the status of the message to send.
     * @param exception the exception that caused the abnormal status.
     */
    protected void send(ResponseStatus status, Throwable exception) {
        write(Protocol.response(status, exception));
    }

    /**
     * sends a response to a request.
     * @param status the status of the message to send.
     */
    protected void send(ResponseStatus status) {
        write(Protocol.response(status));
    }
}