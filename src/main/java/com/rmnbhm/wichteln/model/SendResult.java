package com.rmnbhm.wichteln.model;

public class SendResult {
    private final ParticipantsMatch match;
    private final boolean isSuccess;

    public SendResult(ParticipantsMatch match, boolean isSuccess) {
        this.match = match;
        this.isSuccess = isSuccess;
    }

    public ParticipantsMatch getMatch() {
        return match;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
