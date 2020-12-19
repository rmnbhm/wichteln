package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class WichtelnMailer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WichtelnMailer.class);

    private final WichtelnMailCreator mailCreator;
    private final WichtelnMailDispatcher mailDispatcher;

    public WichtelnMailer(WichtelnMailCreator mailCreator, WichtelnMailDispatcher mailDispatcher) {
        this.mailCreator = mailCreator;
        this.mailDispatcher = mailDispatcher;
    }

    public SendResult send(Event event, ParticipantsMatch match) {
        MimeMessage mail = mailCreator.createMessage(event, match);
        LOGGER.debug("Created mail for {} matching {}", event, match);
        boolean isSuccess = false;
        try {
            mailDispatcher.send(mail).get(4, TimeUnit.SECONDS);
            LOGGER.debug("Sent mail for {} matching {}", event, match);
            isSuccess = true;
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            LOGGER.debug("Failed to sent mail for {} matching {}", event, match, e);
        }
        return new SendResult(match, isSuccess);
    }
}
