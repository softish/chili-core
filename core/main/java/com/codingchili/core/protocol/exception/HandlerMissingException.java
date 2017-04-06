package com.codingchili.core.protocol.exception;

import com.codingchili.core.context.CoreException;
import com.codingchili.core.protocol.ResponseStatus;

import static com.codingchili.core.configuration.CoreStrings.getHandlerMissing;

/**
 * @author Robin Duda
 *         <p>
 *         Throw when a requested handler does not exist.
 */
public class HandlerMissingException extends CoreException {

    public HandlerMissingException(String handler) {
        super(getHandlerMissing(handler), ResponseStatus.ERROR);
    }
}
