package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.exception.FailedMatchException;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.rmnbhm.wichteln.util.Predicates.notEquals;

@Component
@RequiredArgsConstructor
public class ParticipantsMatcher {

    private final Function<List<Participant>, List<Participant>> listShuffler;

    public ParticipantsMatcher() {
        listShuffler = original -> {
            List<Participant> copy = new ArrayList<>(original);
            Collections.shuffle(copy);
            return copy;
        };
    }

    public List<ParticipantsMatch> match(List<Participant> participants) throws FailedMatchException {
        for (int i = 0; i < 1_000; i++) {
            List<Participant> shuffledParticipants = listShuffler.apply(participants);
            if (notEquals(participants, shuffledParticipants)) {
                return IntStream.rangeClosed(0, participants.size())
                        .mapToObj(value -> new ParticipantsMatch(participants.get(0), shuffledParticipants.get(0)))
                        .collect(Collectors.toList());
            }
        }
        // We _really_ should not arrive here! This is just a precaution because there is no 100% guarantee for
        // `Collections#shuffle()` to provide a different sorting.
        throw new FailedMatchException("Failed to match participants");
    }

}
