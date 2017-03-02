package com.codingchili.core.storage;

import com.codingchili.core.configuration.CoreStrings;
import com.codingchili.core.context.CoreException;

/**
 * @author Robin Duda
 *
 * Throw when an entity could not be found in storage.
 */
public class ValueMissingException extends CoreException {
    public ValueMissingException(Object key) {
        super(CoreStrings.getMissingEntity(key.toString()));
    }
}
