package com.rmnbhm.wichteln.exception;

public class WichtelnMailTransmissionException extends RuntimeException {

    public WichtelnMailTransmissionException() {
        super("Failed to send mail to event host");
    }
}
