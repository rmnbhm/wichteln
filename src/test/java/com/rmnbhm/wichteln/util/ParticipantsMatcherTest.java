package com.rmnbhm.wichteln.util;


import com.rmnbhm.wichteln.exception.FailedMatchException;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.service.ParticipantsMatcher;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static com.rmnbhm.wichteln.util.Predicates.notEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParticipantsMatcherTest {

    @Test
    public void shouldShuffle() throws FailedMatchException {
        ParticipantsMatcher participantsMatcher = new ParticipantsMatcher();
        Participant a = new Participant();
        a.setFirstName("a");
        Participant b = new Participant();
        b.setFirstName("b");
        Participant c = new Participant();
        c.setFirstName("v");

        List<ParticipantsMatch> participantsMatches = participantsMatcher.match(List.of(a, b, c));

        assertThat(participantsMatches).allMatch(match -> notEquals(match.getA(), match.getB()));
    }

    @Test
    public void shouldFailWhenNoMatchingCanBeProvided() {
        Function<List<Participant>, List<Participant>> noOpShuffler = Function.identity();
        ParticipantsMatcher participantsMatcher = new ParticipantsMatcher(noOpShuffler);
        Participant a = new Participant();
        a.setFirstName("a");
        Participant b = new Participant();
        b.setFirstName("b");
        Participant c = new Participant();
        c.setFirstName("v");

        assertThatThrownBy(() -> participantsMatcher.match(List.of(a, b, c))).isInstanceOf(FailedMatchException.class);
    }

}