package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class AsyncHostMailer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncHostMailer.class);

    private final HostMailCreator hostMailCreator;
    private final AsyncJavaMailSender mailSender;

    public AsyncHostMailer(HostMailCreator hostMailCreator, AsyncJavaMailSender mailSender) {
        this.hostMailCreator = hostMailCreator;
        this.mailSender = mailSender;
    }

    public CompletableFuture<SendResult> send(Event event, List<SendResult> sendResults) {
        MimeMessage hostMessage = hostMailCreator.createHostMessage(event, sendResults);
        LOGGER.debug(
                "Created mail for {} informing {} about {}",
                event,
                event.getHost(),
                sendResults.stream().map(SendResult::toString).collect(Collectors.joining(", "))
        );
        return mailSender.send(hostMessage).handle(
                (__, throwable) -> {
                    String name = event.getHost().getName();
                    String email = event.getHost().getEmail();
                    return throwable == null ? SendResult.success(name, email) : SendResult.failure(name, email);
                }
        );
    }
}
