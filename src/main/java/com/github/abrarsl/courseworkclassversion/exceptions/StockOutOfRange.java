package com.github.abrarsl.courseworkclassversion.exceptions;

public class StockOutOfRange extends Exception {
    public StockOutOfRange() {
        super();
    }

    public StockOutOfRange(String message) {
        super(message);
    }

    public StockOutOfRange(String message, Throwable cause) {
        super(message, cause);
    }
}
