package com.github.abrarsl.courseworkclassversion.exceptions;

public class InvalidFileDataException extends Exception {
    public InvalidFileDataException() {
        super();
    }

    public InvalidFileDataException(String message) {
        super(message);
    }

    public InvalidFileDataException(String message, Throwable cause) {
        super(message, cause);
    }
}