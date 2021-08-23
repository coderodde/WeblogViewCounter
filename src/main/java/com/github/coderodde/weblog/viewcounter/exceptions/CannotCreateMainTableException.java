package com.github.coderodde.weblog.viewcounter.exceptions;

/**
 * This class implements the checked exception type for reporting the situation
 * where the main database table cannot be created.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 22, 2021)
 * @since 1.6 (Aug 22, 2021)
 */
public final class CannotCreateMainTableException extends Exception {
    
    public CannotCreateMainTableException(Exception cause) {
        super(cause);
    }
}
