package com.codingchili.core.security;

/**
 * @author Robin Duda
 * <p>
 * Determines if text matching a regex should be replaced or if the
 * validation should fail.
 */
public enum RegexAction {
    REJECT, REPLACE
}