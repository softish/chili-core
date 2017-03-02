package com.codingchili.core.storage;

import com.codingchili.core.configuration.CoreStrings;
import com.codingchili.core.context.CoreException;

/**
 * @author Robin Duda
 *
 * Throw when the replace operation cannot complete as there is nothing to replace.
 */
public class NothingToReplaceException extends CoreException {

    NothingToReplaceException(Object key) {
        super(CoreStrings.getNothingToReplaceException(key.toString()));
    }
}
