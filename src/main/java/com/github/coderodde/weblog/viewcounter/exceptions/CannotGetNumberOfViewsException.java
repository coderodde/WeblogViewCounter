package com.github.coderodde.weblog.viewcounter.exceptions;

/**
 * This checked exception class defines the type for reporting failing 
 * operations on reading view counts.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 23, 2021)
 * @since 1.6 (Aug 23, 2021)
 */
public final class CannotGetNumberOfViewsException extends Exception {
    
    public CannotGetNumberOfViewsException(Exception cause) {
        super(cause);
    }
}
