package com.github.abrarsl.courseworkclassversion.exceptions;

public class StockOutOfRangeException extends Exception {
    public StockOutOfRangeException() {
        super();
    }

    public StockOutOfRangeException(String message) {
        super(message);
    }

    public StockOutOfRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
