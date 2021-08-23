package com.github.coderodde.weblog.viewcounter.exceptions;

/**
 * This checked exception class defines the type for reporting failed view count
 * addition operations.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 23, 2021)
 * @since 1.6 (Aug 23, 2021)
 */
public final class CannotAddViewException extends Exception {
    
    public CannotAddViewException(Exception cause) {
        super(cause);
    }
}
