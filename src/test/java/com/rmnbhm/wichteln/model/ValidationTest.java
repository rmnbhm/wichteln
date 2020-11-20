package com.rmnbhm.wichteln.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.money.Monetary;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        Event acdcSanta = new Event();
        acdcSanta.setTitle("AC/DC Secret Santa");
        acdcSanta.setDescription("There's gonna be some santa'ing");
        acdcSanta.setMonetaryAmount(validMonetaryAmount());
        acdcSanta.setLocalDateTime(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        acdcSanta.setPlace("Sydney");
        acdcSanta.setHost(validHost());
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
        return acdcSanta;
    }

    private Participant validParticipant() {
        Participant angusYoung = new Participant();
        angusYoung.setName("Angus Young");
        angusYoung.setEmail("angusyoung@acdc.net");
        return angusYoung;
    }

    private Host validHost() {
        Host georgeYoung = new Host();
        georgeYoung.setName("George Young");
        georgeYoung.setEmail("angusyoung@acdc.net");
        return georgeYoung;
    }

    private MonetaryAmount validMonetaryAmount() {
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        monetaryAmount.setCurrency(Monetary.getCurrency("AUD"));
        monetaryAmount.setNumber(BigDecimal.valueOf(78.50));
        return monetaryAmount;
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
    public void shouldFailEventWithInvalidMonetaryAmount() {
        Event event = validEvent();
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        event.setMonetaryAmount(monetaryAmount);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldMonetaryAmountWithNegativeNumber() {
        MonetaryAmount monetaryAmount = validMonetaryAmount();
        monetaryAmount.setNumber(BigDecimal.valueOf(-1));

        assertThat(validator.validate(monetaryAmount)).isNotEmpty();
    }

    @Test
    public void shouldFailMonetaryAmountWithNullNumber() {
        MonetaryAmount monetaryAmount = validMonetaryAmount();
        monetaryAmount.setNumber(null);

        assertThat(validator.validate(monetaryAmount)).isNotEmpty();
    }

    @Test
    public void shouldFailMonetaryAmountWithNullCurrency() {
        MonetaryAmount monetaryAmount = validMonetaryAmount();
        monetaryAmount.setCurrency(null);

        assertThat(validator.validate(monetaryAmount)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithPastLocalDateTime() {
        Event event = validEvent();
        LocalDateTime pastDate = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
        event.setLocalDateTime(pastDate);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithNullLocalDateTime() {
        Event event = validEvent();
        event.setLocalDateTime(null);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithTooLongPlace() {
        Event event = validEvent();
        event.setPlace(event.getPlace().repeat(100));

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithNullPlace() {
        Event event = validEvent();
        event.setPlace(null);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithEmptyPlace() {
        Event event = validEvent();
        event.setPlace("");

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithWhitespacePlace() {
        Event event = validEvent();
        event.setPlace(" ");

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithNullHost() {
        Event event = validEvent();
        event.setHost(null);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithInvalidHost() {
        Event event = validEvent();
        event.setHost(new Host());

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldAcceptValidHost() {
        Host host = validHost();

        assertThat(validator.validate(host)).isEmpty();
    }

    @Test
    public void shouldFailHostWithTooLongName() {
        Host host = validHost();
        host.setName(host.getName().repeat(20));

        assertThat(validator.validate(host)).isNotEmpty();
    }

    @Test
    public void shouldFailHostWithEmptyName() {
        Host host = validHost();
        host.setName("");

        assertThat(validator.validate(host)).isNotEmpty();
    }

    @Test
    public void shouldFailHostWithWhitespaceName() {
        Host host = validHost();
        host.setName(" ");

        assertThat(validator.validate(host)).isNotEmpty();
    }

    @Test
    public void shouldFailHostWithNullName() {
        Host host = validHost();
        host.setName(null);

        assertThat(validator.validate(host)).isNotEmpty();
    }

    @Test
    public void shouldFailHostWithInvalidEmail() {
        Host host = validHost();
        host.setEmail("not an email address");

        assertThat(validator.validate(host)).isNotEmpty();
    }

    @Test
    public void shouldFailHostWithEmptyEmail() {
        Host host = validHost();
        host.setEmail("");

        assertThat(validator.validate(host)).isNotEmpty();
    }

    @Test
    public void shouldFailHostWithWhitespaceEmail() {
        Host host = validHost();
        host.setEmail(" ");

        assertThat(validator.validate(host)).isNotEmpty();
    }

    @Test
    public void shouldFailHostWithNullEmail() {
        Host host = validHost();
        host.setEmail(null);

        assertThat(validator.validate(host)).isNotEmpty();
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
                .mapToObj(value -> {
                    Participant participant = new Participant();
                    participant.setName("Name" + value);
                    participant.setEmail("Email@" + value);
                    return participant;
                }).collect(Collectors.toList());
        event.setParticipants(oneParticipantTooFew);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithMoreThanOneHundredParticipants() {
        Event event = validEvent();
        List<Participant> oneParticipantTooMany = IntStream.rangeClosed(1, 101)
                .mapToObj(value -> {
                    Participant participant = new Participant();
                    participant.setName("Name" + value);
                    participant.setEmail("Email@" + value);
                    return participant;
                }).collect(Collectors.toList());
        event.setParticipants(oneParticipantTooMany);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldFailEventWithInvalidParticipant() {
        Event event = validEvent();
        Participant participant = new Participant();
        participant.setName(null);
        participant.setEmail(null);
        List<Participant> participants = new ArrayList<>();
        participants.add(participant);
        participants.addAll(event.getParticipants().subList(1, event.getParticipants().size() - 1));
        event.setParticipants(participants);

        assertThat(validator.validate(event)).isNotEmpty();
    }

    @Test
    public void shouldAcceptValidParticipant() {
        Participant participant = validParticipant();

        assertThat(validator.validate(participant)).isEmpty();
    }

    @Test
    public void shouldFailParticipantWithTooLongName() {
        Participant participant = validParticipant();
        participant.setName(participant.getName().repeat(20));

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithEmptyName() {
        Participant participant = validParticipant();
        participant.setName("");

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithWhitespaceName() {
        Participant participant = validParticipant();
        participant.setName(" ");

        assertThat(validator.validate(participant)).isNotEmpty();
    }

    @Test
    public void shouldFailParticipantWithNullName() {
        Participant participant = validParticipant();
        participant.setName(null);

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