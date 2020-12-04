package com.rmnbhm.wichteln.exception;

public class WichtelnMailCreationException extends RuntimeException {

    public WichtelnMailCreationException() {
        super("Failed to create mail");
    }
}
