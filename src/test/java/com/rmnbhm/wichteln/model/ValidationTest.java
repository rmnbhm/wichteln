package com.rmnbhm.wichteln.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationTest {

    private Validator validator;
    private ValidatorFactory validatorFactory;

    @BeforeEach
    public void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    public void close() {
        validatorFactory.close();
    }

    private Event validEvent() {
        return Event.builder()
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
    }

    private Participant validParticipant() {
        return Participant.builder()
                .firstName("Angus")
                .lastName("Young")
                .email("angusyoung@acdc.net")
                .build();
    }


    @Test
    public void shouldAcceptValidEvent() {
        Event event = validEvent();

        Set<ConstraintViolation<Event>> violations = validator.validate(event);

        assertThat(violations).isEmpty();
    }

    @Test
    public void shouldFailEventWithTooLongTitle() {
        Event event = validEvent();
        event.setTitle(event.getTitle().repeat(100));

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithNullTitle() {
        Event event = validEvent();
        event.setTitle(null);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithEmptyTitle() {
        Event event = validEvent();
        event.setTitle("");

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithWhitespaceTitle() {
        Event event = validEvent();
        event.setTitle(" ");

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithTooLongDescription() {
        Event event = validEvent();
        event.setDescription(event.getDescription().repeat(1000));

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithNullDescription() {
        Event event = validEvent();
        event.setDescription(null);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithEmptyDescription() {
        Event event = validEvent();
        event.setDescription("");

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithWhitespaceDescription() {
        Event event = validEvent();
        event.setTitle(" ");

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithNegativeMonetaryAmount() {
        Event event = validEvent();
        event.setMonetaryAmount(-1);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithNullMonetaryAmount() {
        Event event = validEvent();
        event.setMonetaryAmount(null);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithPastHeldAt() {
        Event event = validEvent();
        Date pastDate = Date.from(event.getHeldAt().toInstant().minus(2, ChronoUnit.DAYS));
        event.setHeldAt(pastDate);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithNullHeldAt() {
        Event event = validEvent();
        event.setHeldAt(null);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithNullParticipants() {
        Event event = validEvent();
        event.setParticipants(null);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithLessThanThreeParticipants() {
        Event event = validEvent();
        List<Participant> oneParticipantTooFew = IntStream.rangeClosed(1, 2)
                .mapToObj(value ->
                        Participant.builder()
                                .firstName("First Name" + value)
                                .lastName("Last Name" + value)
                                .email("Email@" + value)
                                .build()
                ).collect(Collectors.toList());
        event.setParticipants(oneParticipantTooFew);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithMoreThanOneHundredParticipants() {
        Event event = validEvent();
        List<Participant> oneParticipantTooMany = IntStream.rangeClosed(1, 101)
                .mapToObj(value ->
                        Participant.builder()
                                .firstName("First Name" + value)
                                .lastName("Last Name" + value)
                                .email("Email@" + value)
                                .build()
                ).collect(Collectors.toList());
        event.setParticipants(oneParticipantTooMany);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithInvalidParticipant() {
        Event event = validEvent();
        event.addParticipant(Participant.builder().build());

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldAcceptValidParticipant() {
        Participant participant = validParticipant();

        assertThat(validator.validate(participant)).isEmpty();
    }

    @Test
    public void shouldFailParticipantWithTooLongFirstName() {
        Participant participant = validParticipant();
        participant.setFirstName(participant.getFirstName().repeat(20));

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithEmptyFirstName() {
        Participant participant = validParticipant();
        participant.setFirstName("");

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithWhitespaceFirstName() {
        Participant participant = validParticipant();
        participant.setFirstName(" ");

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithNullFirstName() {
        Participant participant = validParticipant();
        participant.setFirstName(null);

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithTooLongLastName() {
        Participant participant = validParticipant();
        participant.setLastName(participant.getLastName().repeat(20));

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithEmptyLastName() {
        Participant participant = validParticipant();
        participant.setLastName("");

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithWhitespaceLastName() {
        Participant participant = validParticipant();
        participant.setFirstName(" ");

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithNullLastName() {
        Participant participant = validParticipant();
        participant.setFirstName(null);

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithInvalidEmail() {
        Participant participant = validParticipant();
        participant.setEmail("not an email address");

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithEmptyEmail() {
        Participant participant = validParticipant();
        participant.setEmail("");

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithWhitespaceEmail() {
        Participant participant = validParticipant();
        participant.setEmail(" ");

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithNullEmail() {
        Participant participant = validParticipant();
        participant.setEmail(null);

        assertThat(validator.validate(participant)).isNotEmpty();
    }

}