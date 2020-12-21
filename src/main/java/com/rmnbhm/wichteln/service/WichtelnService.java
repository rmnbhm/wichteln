package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.SendResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WichtelnService {

    private final ParticipantsMatcher matcher;
    private final WichtelnMailer mailSender;

    public WichtelnService(ParticipantsMatcher matcher, WichtelnMailer mailSender) {
        this.matcher = matcher;
        this.mailSender = mailSender;
    }

    public List<SendResult> save(Event event) {
        List<ParticipantsMatch> matches = matcher.match(event.getParticipants());
        List<SendResult> list = new ArrayList<>();
        for (ParticipantsMatch match : matches) {
            SendResult sendResult = mailSender.send(event, match);
            list.add(sendResult);
        }
        return list;
    }
}
