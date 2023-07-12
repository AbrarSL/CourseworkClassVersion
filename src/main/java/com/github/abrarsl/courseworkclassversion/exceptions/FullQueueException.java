package com.github.abrarsl.courseworkclassversion.exceptions;

public class FullQueueException extends Exception {
    public FullQueueException() {
        super();
    }

    public FullQueueException(String message) {
        super(message);
    }

    public FullQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
