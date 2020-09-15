package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WichtelnService {

    private final ParticipantsMatcher matcher;
    private final WichtelnMailCreator mailCreator;
    private final JavaMailSender mailSender;

    public void save(Event event) {
        List<ParticipantsMatch> matches = matcher.match(event.getParticipants());
        matches.forEach(match -> mailSender.send(mailCreator.createMessage(event, match)));
    }
}
