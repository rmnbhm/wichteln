package com.rmnbhm.wichteln.exception;

public class IllegalWichtelnMailStateException extends RuntimeException {

    public IllegalWichtelnMailStateException(String field) {
        super(String.format("Could not retrieve '%s' from MimeMessage", field));
    }
}
