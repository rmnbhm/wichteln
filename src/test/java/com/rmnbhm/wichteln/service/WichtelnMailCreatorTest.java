package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.TestData;
import com.rmnbhm.wichteln.model.*;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    public void shouldHandleToAndFromCorrectly() {
        Event acdcSanta = TestData.event().asObject();
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new Donor(acdcSanta.getParticipants().get(0)), new Recipient(acdcSanta.getParticipants().get(1))
        );

        WichtelnMail mail = wichtelnMailCreator.createMessage(acdcSanta, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.to()).isEqualTo("angusyoung@acdc.net");
    }

    @Test
    public void shouldHandleEventDataCorrectly() {
        Event acdcSanta = TestData.event().asObject();
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new Donor(acdcSanta.getParticipants().get(0)), new Recipient(acdcSanta.getParticipants().get(1))
        );

        WichtelnMail mail = wichtelnMailCreator.createMessage(acdcSanta, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.subject()).isEqualTo("You have been invited to wichtel at AC/DC Secret Santa");
        assertThat(mail.body())
                .isEqualTo(
                        "Hey Angus Young,\n" +
                        "\n" +
                        "You have been invited to wichtel at AC/DC Secret Santa (https://wichteln.rmnbhm.com/about)!\n" +
                        "You're therefore asked to give a gift to Malcolm Young. The gift's monetary value should not exceed AUD 78.50.\n" +
                        String.format(
                                "The event will take place at Sydney Harbor on %s at %s local time.\n",
                                LocalDate.from(acdcSanta.getLocalDateTime()),
                                LocalTime.from(acdcSanta.getLocalDateTime()).truncatedTo(ChronoUnit.MINUTES)
                        ) +
                        "\n" +
                        "Here's what the event's host says about it:\n" +
                        "\n" +
                        "\"There's gonna be some santa'ing\"\n" +
                        "\n" +
                        "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.\n" +
                        "\n" +
                        "This mail was generated using https://wichteln.rmnbhm.com"
                );
    }

}