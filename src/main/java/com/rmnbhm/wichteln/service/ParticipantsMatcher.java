package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class ParticipantsMatcher {

    public List<ParticipantsMatch> match(List<Participant> participants) {
        List<Participant> copy = new ArrayList<>(participants);
        Random random = new Random();
        while (true) {
            Collections.rotate(copy, random.nextInt());
            try {
                return IntStream.range(0, participants.size())
                        .mapToObj(i ->
                                new ParticipantsMatch(new Donor(participants.get(i)), new Recipient(copy.get(i)))
                        ).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {
                log.debug("Collision while shuffling participants to generate matches");
            }
        }
    }

}