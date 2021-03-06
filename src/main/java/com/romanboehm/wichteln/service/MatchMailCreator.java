package com.romanboehm.wichteln.service;

import com.romanboehm.wichteln.exception.WichtelnMailCreationException;
import com.romanboehm.wichteln.model.Event;
import com.romanboehm.wichteln.model.ParticipantsMatch;
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

@Component
public class MatchMailCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchMailCreator.class);

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public MatchMailCreator(TemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public MimeMessage createMatchMessage(Event event, ParticipantsMatch match) {
        try {
            ParticipantsMatch.Donor donor = match.getDonor();
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
            message.setSubject(String.format("You have been invited to wichtel at '%s'", event.getTitle()));
            message.setFrom("wichteln@romanboehm.com");
            message.setTo(donor.getEmail());

            Context ctx = new Context();
            ctx.setVariable("event", event);
            ctx.setVariable("donor", donor.getName());
            ctx.setVariable("recipient", match.getRecipient().getName());
            String textContent = templateEngine.process("matchmail.txt", ctx);
            message.setText(textContent);

            LOGGER.debug("Created mail for {} matching {}", event, match);
            return mimeMessage;
        } catch (MessagingException e) {
            // Re-throw as custom `RuntimeException` to be handled by upstream by `ErrorController`
            throw new WichtelnMailCreationException();
        }
    }
}
