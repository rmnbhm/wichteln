package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.WichtelnMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            WichtelnMail mail = mailCreator.createMessage(event, match, WichtelnMailCreator.MailMode.TEXT);
            LOGGER.debug("Created {} for {} matching {}", mail, event, match);
            mailSender.send(mail.getMimeMessage());
            LOGGER.debug("Sent {} for {} matching {}", mail, event, match);
        });
    }

    public WichtelnMail createPreview(Event event) {
        Participant exampleDonor = new Participant();
        exampleDonor.setName("Angus Young");
        exampleDonor.setEmail("angusyoung@acdc.net");
        Participant exampleRecipient = new Participant();
        exampleRecipient.setName("Phil Rudd");
        exampleRecipient.setEmail("philrudd@acdc.net");
        WichtelnMail preview = mailCreator.createMessage(
                event,
                new ParticipantsMatch(
                        new ParticipantsMatch.Donor(exampleDonor),
                        new ParticipantsMatch.Recipient(exampleRecipient)
                ),
                WichtelnMailCreator.MailMode.HTML
        );
        LOGGER.debug("Created {} as preview for {}", preview, event);
        return preview;
    }
}
