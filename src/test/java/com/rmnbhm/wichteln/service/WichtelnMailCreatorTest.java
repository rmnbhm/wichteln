package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.TestData;
import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=testuser",
        "spring.mail.password=testpassword",
        "spring.mail.protocol=smtp",
})
public class WichtelnMailCreatorTest {

    @Autowired
    private WichtelnMailCreator wichtelnMailCreator;

    @Test
    public void shouldHandleToAndFromCorrectly() throws MessagingException {
        Event acdcSanta = TestData.event().asObject();
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new Donor(acdcSanta.getParticipants().get(0)), new Recipient(acdcSanta.getParticipants().get(1))
        );

        MimeMessage mail = wichtelnMailCreator.createMessage(acdcSanta, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.getRecipients(Message.RecipientType.TO))
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }

    @Test
    public void shouldHandleEventDataCorrectly() throws IOException, MessagingException {
        Event acdcSanta = TestData.event().asObject();
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new Donor(acdcSanta.getParticipants().get(0)), new Recipient(acdcSanta.getParticipants().get(1))
        );

        MimeMessage mail = wichtelnMailCreator.createMessage(acdcSanta, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("You have been invited to wichtel at AC/DC Secret Santa");
        MatcherAssert.assertThat(mail.getContent().toString(), Matchers.stringContainsInOrder(
                "Hey Angus Young,",
                "You have been invited to wichtel at AC/DC Secret Santa (https://wichteln.rmnbhm.com/about)!",
                "You're therefore asked to give a gift to Malcolm Young. The gift's monetary value should not exceed AUD 78.50.",
                String.format(
                        "The event will take place at Sydney Harbor on %s at %s local time.",
                        LocalDate.from(acdcSanta.getLocalDateTime()),
                        LocalTime.from(acdcSanta.getLocalDateTime()).truncatedTo(ChronoUnit.MINUTES)
                ),
                "Here's what the event's host says about it:",
                "\"There's gonna be some santa'ing\"",
                "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.",
                "This mail was generated using https://wichteln.rmnbhm.com"
        ));
    }

}