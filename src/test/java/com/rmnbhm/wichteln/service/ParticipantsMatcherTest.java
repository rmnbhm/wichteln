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
        Participant a = Participant.builder().firstName("a").build();
        Participant b = Participant.builder().firstName("b").build();
        Participant c = Participant.builder().firstName("c").build();

        List<ParticipantsMatch> participantsMatches = participantsMatcher.match(List.of(a, b, c));

        assertThat(participantsMatches).allSatisfy(match ->
                assertThat(match.getDonor().getParticipant()).isNotEqualTo(match.getRecipient().getParticipant())
        );
    }
}