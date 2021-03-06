package com.codingchili.core.security.exception;

import com.codingchili.core.configuration.CoreStrings;
import com.codingchili.core.context.CoreException;

/**
 * @author Robin Duda
 * <p>
 * Throw when a hash comparison has failed.
 */
public class HashMismatchException extends CoreException {
    public HashMismatchException() {
        super(CoreStrings.getHashMismatchException());
    }
}
