package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.*;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        Event acdcSanta = new Event();
        acdcSanta.setTitle("AC/DC Secret Santa");
        acdcSanta.setDescription("There's gonna be some santa'ing");
        acdcSanta.setLocalDateTime(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        acdcSanta.setPlace("Sydney Harbor");
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        monetaryAmount.setCurrency(Monetary.getCurrency("AUD"));
        monetaryAmount.setNumber(BigDecimal.valueOf(78.50));
        acdcSanta.setMonetaryAmount(monetaryAmount);
        Host georgeYoung = new Host();
        georgeYoung.setName("George Young");
        georgeYoung.setEmail("georgeyoung@acdc.net");
        acdcSanta.setHost(georgeYoung);
        Participant angusYoung = new Participant();
        angusYoung.setName("Angus Young");
        angusYoung.setEmail("angusyoung@acdc.net");
        Participant malcolmYoung = new Participant();
        malcolmYoung.setName("Malcolm Young");
        malcolmYoung.setEmail("malcolmyoung@acdc.net");
        Participant philRudd = new Participant();
        philRudd.setName("Phil Rudd");
        philRudd.setEmail("philrudd@acdc.net");
        acdcSanta.setParticipants(List.of(angusYoung, malcolmYoung, philRudd));
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new Donor(acdcSanta.getParticipants().get(0)), new Recipient(acdcSanta.getParticipants().get(1))
        );

        WichtelnMail mail = wichtelnMailCreator.createMessage(acdcSanta, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.to()).isEqualTo("angusyoung@acdc.net");
    }

    @Test
    public void shouldHandleEventDataCorrectly() {
        LocalDateTime localDateTime = LocalDateTime.now().plus(1, ChronoUnit.DAYS);

        Event acdcSanta = new Event();
        acdcSanta.setTitle("AC/DC Secret Santa");
        acdcSanta.setDescription("There's gonna be some santa'ing");
        acdcSanta.setLocalDateTime(localDateTime);
        acdcSanta.setPlace("Sydney Harbor");
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        monetaryAmount.setCurrency(Monetary.getCurrency("AUD"));
        monetaryAmount.setNumber(BigDecimal.valueOf(78.50));
        acdcSanta.setMonetaryAmount(monetaryAmount);
        Host georgeYoung = new Host();
        georgeYoung.setName("George Young");
        georgeYoung.setEmail("georgeyoung@acdc.net");
        acdcSanta.setHost(georgeYoung);
        Participant angusYoung = new Participant();
        angusYoung.setName("Angus Young");
        angusYoung.setEmail("angusyoung@acdc.net");
        Participant malcolmYoung = new Participant();
        malcolmYoung.setName("Malcolm Young");
        malcolmYoung.setEmail("malcolmyoung@acdc.net");
        Participant philRudd = new Participant();
        philRudd.setName("Phil Rudd");
        philRudd.setEmail("philrudd@acdc.net");
        acdcSanta.setParticipants(List.of(angusYoung, malcolmYoung, philRudd));
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new Donor(acdcSanta.getParticipants().get(0)), new Recipient(acdcSanta.getParticipants().get(1))
        );

        WichtelnMail mail = wichtelnMailCreator.createMessage(acdcSanta, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.subject()).isEqualTo("You have been invited to wichtel at AC/DC Secret Santa");
        assertThat(mail.text())
                .isEqualTo(
                        "Hey Angus Young,\n" +
                        "\n" +
                        "You have been invited to wichtel at AC/DC Secret Santa (https://wichteln.rmnbhm.com/about)!\n" +
                        "You're therefore asked to give a gift to Malcolm Young. The gift's monetary value should not exceed AUD 78.50.\n" +
                        String.format(
                                "The event will take place at Sydney Harbor on %s at %s local time.\n",
                                LocalDate.from(localDateTime),
                                LocalTime.from(localDateTime).truncatedTo(ChronoUnit.MINUTES)
                        ) +
                        "\n" +
                        "Here's what the event's host says about it:\n" +
                        "\n" +
                        "There's gonna be some santa'ing\n" +
                        "\n" +
                        "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.\n" +
                        "\n" +
                        "This mail was generated using https://wichteln.rmnbhm.com"
                );
    }

}