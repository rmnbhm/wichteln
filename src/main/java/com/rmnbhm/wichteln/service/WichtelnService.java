package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WichtelnService {

    private final ParticipantsMatcher matcher;
    private final WichtelnMailer wichtelnMailer;

    public WichtelnService(ParticipantsMatcher matcher, WichtelnMailer wichtelnMailer) {
        this.matcher = matcher;
        this.wichtelnMailer = wichtelnMailer;
    }

    public void save(Event event) {
        List<ParticipantsMatch> matches = matcher.match(event.getParticipants());
        wichtelnMailer.send(event, matches);
    }
}
