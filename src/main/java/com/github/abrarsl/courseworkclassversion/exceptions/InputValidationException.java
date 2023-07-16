package com.github.abrarsl.courseworkclassversion.exceptions;

public class InputValidationException extends Exception {
    public InputValidationException() {
        super();
    }

    public InputValidationException(String message) {
        super(message);
    }

    public InputValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
