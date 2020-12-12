package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class WichtelnService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WichtelnService.class);
    private final ParticipantsMatcher matcher;
    private final WichtelnMailCreator mailCreator;
    private final JavaMailSender mailSender;

    public WichtelnService(ParticipantsMatcher matcher, WichtelnMailCreator mailCreator, JavaMailSender mailSender) {
        this.matcher = matcher;
        this.mailCreator = mailCreator;
        this.mailSender = mailSender;
    }

    public void save(Event event) {
        List<ParticipantsMatch> matches = matcher.match(event.getParticipants());
        matches.forEach(match -> {
            MimeMessage mail = mailCreator.createMessage(event, match);
            LOGGER.debug("Created mail for {} matching {}", event, match);
            mailSender.send(mail);
            LOGGER.debug("Sent mail for {} matching {}", event, match);
        });
    }
}
