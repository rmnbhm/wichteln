package com.rmnbhm.wichteln.validation;

import com.rmnbhm.wichteln.TestData;
import com.rmnbhm.wichteln.model.Participant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticipantValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidParticipant() {
        Participant participant = TestData.participant();

        assertThat(getValidator().validate(participant)).isEmpty();
    }

    @Nested
    public class ParticipantName {

        @Test
        public void shouldFailParticipantWithTooLongName() {
            Participant participant = TestData.participant();
            participant.setName(participant.getName().repeat(20));

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyName() {
            Participant participant = TestData.participant();
            participant.setName("");

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceName() {
            Participant participant = TestData.participant();
            participant.setName(" ");

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullName() {
            Participant participant = TestData.participant();
            participant.setName(null);

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithJavaControlCharactersInName() {
            Participant participant = TestData.participant();
            participant.setName("my\\nname");

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithHtmlInName() {
            Participant participant = TestData.participant();
            participant.setName("my<span>name</span>");

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }
    }

    @Nested
    public class ParticipantEmail {
        @Test
        public void shouldFailParticipantWithInvalidEmail() {
            Participant participant = TestData.participant();
            participant.setEmail("not an email address");

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyEmail() {
            Participant participant = TestData.participant();
            participant.setEmail("");

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceEmail() {
            Participant participant = TestData.participant();
            participant.setEmail(" ");

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullEmail() {
            Participant participant = TestData.participant();
            participant.setEmail(null);

            assertThat(getValidator().validate(participant)).isNotEmpty();
        }
    }

}