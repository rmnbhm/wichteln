package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class ParticipantsMatcher {

    public List<ParticipantsMatch> match(List<Participant> participants) {
        List<Participant> copy = new ArrayList<>(participants);
        Random random = new Random();
        do {
            Collections.rotate(copy, random.nextInt());
        } while (areNotMatchedCorrectly(participants, copy));

        return IntStream.range(0, participants.size())
                .mapToObj(i ->
                        ParticipantsMatch.builder()
                                .donor(new Donor(participants.get(i)))
                                .recipient(new Recipient(copy.get(i)))
                                .build()
                ).collect(Collectors.toList());
    }

    private boolean areNotMatchedCorrectly(List<Participant> participants, List<Participant> copy) {
        return IntStream.range(0, participants.size())
                .anyMatch(i -> participants.get(i).equals(copy.get(i)));
    }

}