package com.app.dev.bikeapp.exception;

public class UnAuthorizedRideException extends RuntimeException {

    public UnAuthorizedRideException(String message) {
        super(message);
    }
}