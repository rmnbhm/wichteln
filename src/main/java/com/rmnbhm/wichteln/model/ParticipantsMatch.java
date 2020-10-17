package com.rmnbhm.wichteln.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Delegate;

@Value
@Builder
public class ParticipantsMatch {
    @NonNull Donor donor;
    @NonNull Recipient recipient;

    public ParticipantsMatch(Donor donor, Recipient recipient) {
        if (donor.getParticipant().equals(recipient.getParticipant())) {
            throw new IllegalArgumentException("Donor and recipient must not be equal");
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


