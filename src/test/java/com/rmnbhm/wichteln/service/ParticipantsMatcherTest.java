package com.rmnbhm.wichteln.service;


import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipantsMatcherTest {

    @Test
    public void shouldShuffle() {
        ParticipantsMatcher participantsMatcher = new ParticipantsMatcher();
        Participant angusYoung = new Participant();
        angusYoung.setName("Angus Young");
        Participant malcolmYoung = new Participant();
        malcolmYoung.setName("Malcolm Young");
        Participant philRudd = new Participant();
        philRudd.setName("Phil Rudd");

        List<ParticipantsMatch> participantsMatches = participantsMatcher.match(
                List.of(angusYoung, malcolmYoung, philRudd)
        );

        assertThat(participantsMatches).allSatisfy(match ->
                assertThat(match.getDonor().getParticipant()).isNotEqualTo(match.getRecipient().getParticipant())
        );
    }
}