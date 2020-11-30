package com.rmnbhm.wichteln.validation;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Host;
import com.rmnbhm.wichteln.model.MonetaryAmount;
import com.rmnbhm.wichteln.model.Participant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class EventValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidEvent() {
        Event event = TestData.event();

        assertThat(getValidator().validate(event)).isEmpty();
    }

    @Nested
    public class EventPlace {

        @Test
        public void shouldFailEventWithTooLongPlace() {
            Event event = TestData.event();
            event.setPlace(event.getPlace().repeat(100));

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullPlace() {
            Event event = TestData.event();
            event.setPlace(null);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyPlace() {
            Event event = TestData.event();
            event.setPlace("");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespacePlace() {
            Event event = TestData.event();
            event.setPlace(" ");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithJavaControlCharactersInPlace() {
            Event event = TestData.event();
            event.setPlace("my\\ndplace");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithHtmlInPlace() {
            Event event = TestData.event();
            event.setPlace("my<span>place</span>");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventDependencies {

        @Test
        public void shouldFailEventWithInvalidMonetaryAmount() {
            Event event = TestData.event();
            MonetaryAmount monetaryAmount = new MonetaryAmount();
            event.setMonetaryAmount(monetaryAmount);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullMonetaryAmount() {
            Event event = TestData.event();
            event.setMonetaryAmount(null);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullHost() {
            Event event = TestData.event();
            event.setHost(null);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithInvalidHost() {
            Event event = TestData.event();
            event.setHost(new Host());

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullParticipants() {
            Event event = TestData.event();
            event.setParticipants(null);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithLessThanThreeParticipants() {
            Event event = TestData.event();
            List<Participant> oneParticipantTooFew = IntStream.rangeClosed(1, 2)
                    .mapToObj(value -> {
                        Participant participant = new Participant();
                        participant.setName("Name" + value);
                        participant.setEmail("Email@" + value);
                        return participant;
                    }).collect(Collectors.toList());
            event.setParticipants(oneParticipantTooFew);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithMoreThanOneHundredParticipants() {
            Event event = TestData.event();
            List<Participant> oneParticipantTooMany = IntStream.rangeClosed(1, 101)
                    .mapToObj(value -> {
                        Participant participant = new Participant();
                        participant.setName("Name" + value);
                        participant.setEmail("Email@" + value);
                        return participant;
                    }).collect(Collectors.toList());
            event.setParticipants(oneParticipantTooMany);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithInvalidParticipant() {
            Event event = TestData.event();
            Participant participant = new Participant();
            participant.setName(null);
            participant.setEmail(null);
            List<Participant> participants = new ArrayList<>();
            participants.add(participant);
            participants.addAll(event.getParticipants().subList(1, event.getParticipants().size() - 1));
            event.setParticipants(participants);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventTitle {

        @Test
        public void shouldFailEventWithTooLongTitle() {
            Event event = TestData.event();
            event.setTitle(event.getTitle().repeat(100));

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullTitle() {
            Event event = TestData.event();
            event.setTitle(null);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyTitle() {
            Event event = TestData.event();
            event.setTitle("");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceTitle() {
            Event event = TestData.event();
            event.setTitle(" ");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithJavaControlCharactersInTitle() {
            Event event = TestData.event();
            event.setTitle("my\\ndtitle");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithHtmlInTitle() {
            Event event = TestData.event();
            event.setTitle("my<span>title</span>");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventDescription {

        @Test
        public void shouldFailEventWithTooLongDescription() {
            Event event = TestData.event();
            event.setDescription(event.getDescription().repeat(1000));

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullDescription() {
            Event event = TestData.event();
            event.setDescription(null);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyDescription() {
            Event event = TestData.event();
            event.setDescription("");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceDescription() {
            Event event = TestData.event();
            event.setDescription(" ");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithJavaControlCharactersInDescription() {
            Event event = TestData.event();
            event.setDescription("my\\ndescription");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithHtmlInDescription() {
            Event event = TestData.event();
            event.setDescription("my<span>description</span>");

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventLocalDateTime {

        @Test
        public void shouldFailEventWithPastLocalDateTime() {
            Event event = TestData.event();
            LocalDateTime pastDate = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
            event.setLocalDateTime(pastDate);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalDateTime() {
            Event event = TestData.event();
            event.setLocalDateTime(null);

            assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

}