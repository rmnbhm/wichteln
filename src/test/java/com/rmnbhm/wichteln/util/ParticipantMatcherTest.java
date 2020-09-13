package com.rmnbhm.wichteln.util;


import com.rmnbhm.wichteln.exception.FailedMatchException;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.service.ParticipantMatcher;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static com.rmnbhm.wichteln.util.Predicates.notEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParticipantMatcherTest {

    @Test
    public void shouldShuffle() throws FailedMatchException {
        ParticipantMatcher participantMatcher = new ParticipantMatcher();
        Participant a = new Participant();
        a.setFirstName("a");
        Participant b = new Participant();
        b.setFirstName("b");
        Participant c = new Participant();
        c.setFirstName("v");

        List<ParticipantsMatch> participantsMatches = participantMatcher.match(List.of(a, b, c));

        assertThat(participantsMatches).allMatch(match -> notEquals(match.getA(), match.getB()));
    }

    @Test
    public void shouldFailWhenNoMatchingCanBeProvided() {
        Function<List<Participant>, List<Participant>> noOpShuffler = Function.identity();
        ParticipantMatcher participantMatcher = new ParticipantMatcher(noOpShuffler);
        Participant a = new Participant();
        a.setFirstName("a");
        Participant b = new Participant();
        b.setFirstName("b");
        Participant c = new Participant();
        c.setFirstName("v");

        assertThatThrownBy(() -> participantMatcher.match(List.of(a, b, c))).isInstanceOf(FailedMatchException.class);
    }

}