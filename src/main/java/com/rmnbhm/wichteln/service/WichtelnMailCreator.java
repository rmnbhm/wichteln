package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WichtelnMailCreator {

    public SimpleMailMessage createMessage(Event event, ParticipantsMatch match) {
        return createMessage(event, match.getDonor(), match.getRecipient());
    }

    private SimpleMailMessage createMessage(Event event, Donor donor, Recipient recipient) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("wichteln@romanboehm.com");
        message.setTo(donor.getEmail());
        message.setSubject(String.format("You have been invited to wichtel at %s", event.getTitle()));
        message.setText(
                String.format(
                        "Give a gift to %s. " +
                                "The gift's monetary value should not exceed %s. The event will be held at %s on %s. " +
                                "Here's what the event's host says about it: %s. " +
                                "If you have any questions, contact %s at %s",
                        recipient.getName(),
                        Money.of(event.getMonetaryAmount().getNumber(), event.getMonetaryAmount().getCurrency()),
                        event.getPlace(),
                        event.getLocalDateTime(),
                        event.getDescription(),
                        event.getHost().getName(),
                        event.getHost().getEmail()
                )
        );
        return message;
    }

}
