package com.github.abrarsl.courseworkclassversion.exceptions;

public class SelectionOutOfRangeException extends Exception {
    public SelectionOutOfRangeException() {
        super();
    }

    public SelectionOutOfRangeException(String message) {
        super(message);
    }

    public SelectionOutOfRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
