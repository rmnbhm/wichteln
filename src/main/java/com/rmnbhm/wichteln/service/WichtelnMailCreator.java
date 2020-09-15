package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
@RequiredArgsConstructor
public class WichtelnMailCreator {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public SimpleMailMessage createMessage(Event event, ParticipantsMatch match) {
        return createMessage(event, match.getDonor(), match.getRecipient());
    }

    private SimpleMailMessage createMessage(Event event, Donor donor, Recipient recipient) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("wichteln@romanboehm.com");
        message.setTo(donor.getEmail());
        message.setSubject(String.format("You have been invited to wichtel at %s", event.getTitle()));
        message.setText(String.join(System.lineSeparator(),
                predefinedDescription(event, recipient),
                "Here's what the event's host says about it:",
                event.getDescription()
        ));
        return message;
    }

    private String predefinedDescription(Event event, Recipient recipient) {
        return String.format(
                "Give a gift to %s %s. The gift's monetary value should not exceed %d. The event will be held on %s",
                recipient.getFirstName(),
                recipient.getLastName(),
                event.getMonetaryAmount(),
                FORMAT.format(event.getHeldAt())
        );
    }
}
