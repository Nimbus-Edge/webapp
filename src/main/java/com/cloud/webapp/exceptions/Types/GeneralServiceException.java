package com.cloud.webapp.exceptions.Types;

public class GeneralServiceException extends RuntimeException {

    public GeneralServiceException(String message) {
        super(message);
    }

    public GeneralServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}