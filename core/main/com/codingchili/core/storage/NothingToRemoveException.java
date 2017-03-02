package com.codingchili.core.storage;

import com.codingchili.core.configuration.CoreStrings;
import com.codingchili.core.context.CoreException;

/**
 * @author Robin Duda
 *
 * Throw when the remove operation cannot be completed as there is nothing to be removed.
 */
public class NothingToRemoveException extends CoreException {
    public NothingToRemoveException(Object key) {
        super(CoreStrings.getNothingToRemoveException(key.toString()));
    }
}
