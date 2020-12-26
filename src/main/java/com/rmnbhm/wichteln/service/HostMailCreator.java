package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.exception.WichtelnMailCreationException;
import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.rmnbhm.wichteln.config.MailConfig.FROM_ADDRESS;

@Component
public class HostMailCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostMailCreator.class);

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public HostMailCreator(TemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public MimeMessage createHostMessage(Event event, List<SendResult> results) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
            message.setSubject(String.format(
                    "The invitations for your Wichteln event '%s' have been sent", event.getTitle()
            ));
            message.setFrom(FROM_ADDRESS);
            message.setTo(event.getHost().getEmail());

            Context ctx = new Context();
            ctx.setVariable("event", event);
            ctx.setVariable("results", results);
            String textContent = templateEngine.process("hostmail.txt", ctx);
            message.setText(textContent);

            LOGGER.debug("Created mail for {} informing {}", event, event.getHost());
            return mimeMessage;
        } catch (MessagingException e) {
            // Re-throw as custom `RuntimeException` to be handled by upstream by `ErrorController`
            throw new WichtelnMailCreationException();
        }
    }
}
