package com.rmnbhm.wichteln.model;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Delegate;

@Value
public class ParticipantsMatch {
    Donor donor;
    Recipient recipient;

    public ParticipantsMatch(@NonNull Donor donor, @NonNull Recipient recipient) {
        if (donor.getParticipant().equals(recipient.getParticipant())) {
            throw new IllegalArgumentException("Donor and recipient must not match");
        }
        this.donor = donor;
        this.recipient = recipient;
    }

    @Value
    public static class Recipient {
        @Delegate
        Participant participant;
    }

    @Value
    public static class Donor {
        @Delegate
        Participant participant;
    }
}


