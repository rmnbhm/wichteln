package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Host;
import com.rmnbhm.wichteln.model.MonetaryAmount;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WichtelnMailCreatorTest {

    private final WichtelnMailCreator wichtelnMailCreator = new WichtelnMailCreator();

    @Test
    public void shouldHandleToAndFromCorrectly() {
        Event acdcSanta = new Event();
        acdcSanta.setTitle("AC/DC Secret Santa");
        acdcSanta.setDescription("There's gonna be some santa'ing");
        acdcSanta.setLocalDateTime(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        acdcSanta.setPlace("Sydney");
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

        SimpleMailMessage mail = wichtelnMailCreator.createMessage(acdcSanta, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.getTo()).containsExactly("angusyoung@acdc.net");
        assertThat(mail.getText())
                .contains("Malcolm Young")
                .doesNotContain("Angus Young");
    }

    @Test
    public void shouldHandleEventDataCorrectly() {
        LocalDateTime localDateTime = LocalDateTime.now().plus(1, ChronoUnit.DAYS);

        Event acdcSanta = new Event();
        acdcSanta.setTitle("AC/DC Secret Santa");
        acdcSanta.setDescription("There's gonna be some santa'ing");
        acdcSanta.setLocalDateTime(localDateTime);
        acdcSanta.setPlace("Sydney");
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

        SimpleMailMessage mail = wichtelnMailCreator.createMessage(acdcSanta, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.getText())
                .contains("AUD 78.50")
                .contains(localDateTime.truncatedTo(ChronoUnit.MINUTES).toString())
                .contains("Sydney")
                .contains("George Young")
                .contains("georgeyoung@acdc.net")
                .contains("There's gonna be some santa'ing");
        assertThat(mail.getSubject()).isEqualTo("You have been invited to wichtel at AC/DC Secret Santa");
    }

    @Test
    public void shouldFormatDescription() {
        LocalDateTime localDateTime = LocalDateTime.now().plus(1, ChronoUnit.DAYS);

        Event acdcSanta = new Event();
        acdcSanta.setTitle("AC/DC Secret Santa");
        acdcSanta.setDescription("There's gonna be some santa'ing...\nAt the show...\r\n\r\ntonight.");
        acdcSanta.setLocalDateTime(localDateTime);
        acdcSanta.setPlace("Sydney");
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

        System.out.println(acdcSanta.toString());

        SimpleMailMessage mail = wichtelnMailCreator.createMessage(acdcSanta, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.getText())
                .contains("> There's gonna be some santa'ing...\r\n> At the show...\r\n> \r\n> tonight");
        assertThat(mail.getSubject()).isEqualTo("You have been invited to wichtel at AC/DC Secret Santa");
    }


}