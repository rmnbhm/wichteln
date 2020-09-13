package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.exception.FailedMatchException;
import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WichtelnService {

    ParticipantMatcher participantMatcher;

    public void save(Event event) throws FailedMatchException {
            List<ParticipantsMatch> participantsMatches = participantMatcher.match(event.getParticipants());
            participantsMatches.forEach(participantsMatch -> {
                // TODO: notify matched participants
            });
    }
}
