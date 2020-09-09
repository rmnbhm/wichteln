package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Match;
import com.rmnbhm.wichteln.util.ParticipantMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WichtelnService {

    private ParticipantMatcher participantMatcher;

    public void save(Event event) {
        List<Match> matches = participantMatcher.match(event);
    }
}
