package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
            SimpleMailMessage message = mailCreator.createMessage(event, match);
            LOGGER.info(
                    "Sent {} to {}",
                    StringUtils.abbreviate(
                            // Escape java since `SimpleMailMessage::toString` includes linebreaks.
                            StringEscapeUtils.escapeJava(message.toString()),
                            50
                    ),
                    match.getDonor()
            );
            mailSender.send(message);
        });
    }

    public SimpleMailMessage createPreview(Event event) {
        Participant exampleDonor = new Participant();
        exampleDonor.setName("Angus Young");
        exampleDonor.setEmail("angusyoung@acdc.net");
        Participant exampleRecipient = new Participant();
        exampleRecipient.setName("Phil Rudd");
        exampleRecipient.setEmail("philrudd@acdc.net");
        return mailCreator.createMessage(
                event,
                new ParticipantsMatch(
                        new ParticipantsMatch.Donor(exampleDonor),
                        new ParticipantsMatch.Recipient(exampleRecipient)
                )
        );
    }
}
