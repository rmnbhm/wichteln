package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WichtelnMailCreatorTest {

    private WichtelnMailCreator wichtelnMailCreator = new WichtelnMailCreator();

    @Test
    public void shouldHandleToAndFromCorrectly() {
        Event event = new Event();
        event.setTitle("AC/DC Secret Santa");
        event.setDescription("There's gonna be some santa'ing");
        event.setHeldAt(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        event.setMonetaryAmount(78);
        Participant angusYoung = new Participant();
        angusYoung.setFirstName("Angus");
        angusYoung.setLastName("Young");
        angusYoung.setEmail("angusyoung@acdc.net");
        Participant malcolmYoung = new Participant();
        malcolmYoung.setFirstName("Malcolm");
        malcolmYoung.setLastName("Young");
        malcolmYoung.setEmail("malcolmyoung@acdc.net");
        event.setParticipants(List.of(angusYoung, malcolmYoung));
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new Donor(event.getParticipants().get(0)), new Recipient(event.getParticipants().get(1))
        );

        SimpleMailMessage mail = wichtelnMailCreator.createMessage(event, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.getTo()).containsExactly("angusyoung@acdc.net");
        assertThat(mail.getText())
                .contains("Malcolm Young")
                .doesNotContain("Angus Young");
    }

    @Test
    public void shouldHandleEventDataCorrectly() {
        Event event = new Event();
        event.setTitle("AC/DC Secret Santa");
        event.setDescription("There's gonna be some santa'ing");
        event.setHeldAt(Date.valueOf("3030-01-01"));
        event.setMonetaryAmount(78);
        Participant angusYoung = new Participant();
        angusYoung.setFirstName("Angus");
        angusYoung.setLastName("Young");
        angusYoung.setEmail("angusyoung@acdc.net");
        Participant malcolmYoung = new Participant();
        malcolmYoung.setFirstName("Malcolm");
        malcolmYoung.setLastName("Young");
        malcolmYoung.setEmail("malcolmyoung@acdc.net");
        event.setParticipants(List.of(angusYoung, malcolmYoung));
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new Donor(event.getParticipants().get(0)), new Recipient(event.getParticipants().get(1))
        );

        SimpleMailMessage mail = wichtelnMailCreator.createMessage(event, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.getText())
                .contains("78")
                .contains("3030-01-01")
                .contains("There's gonna be some santa'ing");
        assertThat(mail.getSubject()).isEqualTo("You have been invited to wichtel at AC/DC Secret Santa");
    }


}