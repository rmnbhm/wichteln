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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WichtelnMailCreatorTest {

    private WichtelnMailCreator wichtelnMailCreator = new WichtelnMailCreator();

    @Test
    public void shouldHandleToAndFromCorrectly() {
        Event event = Event.builder()
                .title("AC/DC Secret Santa")
                .description("There's gonna be some santa'ing")
                .heldAt(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .monetaryAmount(78)
                .participant(Participant.builder()
                        .firstName("Angus")
                        .lastName("Young")
                        .email("angusyoung@acdc.net")
                        .build()
                )
                .participant(Participant.builder()
                        .firstName("Malcolm")
                        .lastName("Young")
                        .email("angusyoung@acdc.net")
                        .build()
                )
                .participant(Participant.builder()
                        .firstName("Phil")
                        .lastName("Rudd")
                        .email("philrudd@acdc.net")
                        .build()
                )
                .build();
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
        Date heldAt = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        Event event = Event.builder()
                .title("AC/DC Secret Santa")
                .description("There's gonna be some santa'ing")
                .heldAt(heldAt)
                .monetaryAmount(78)
                .participant(Participant.builder()
                        .firstName("Angus")
                        .lastName("Young")
                        .email("angusyoung@acdc.net")
                        .build()
                )
                .participant(Participant.builder()
                        .firstName("Malcolm")
                        .lastName("Young")
                        .email("angusyoung@acdc.net")
                        .build()
                )
                .participant(Participant.builder()
                        .firstName("Phil")
                        .lastName("Rudd")
                        .email("philrudd@acdc.net")
                        .build()
                )
                .build();
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new Donor(event.getParticipants().get(0)), new Recipient(event.getParticipants().get(1))
        );

        SimpleMailMessage mail = wichtelnMailCreator.createMessage(event, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.getText())
                .contains("78")
                .contains(heldAt.toString())
                .contains("There's gonna be some santa'ing");
        assertThat(mail.getSubject()).isEqualTo("You have been invited to wichtel at AC/DC Secret Santa");
    }


}