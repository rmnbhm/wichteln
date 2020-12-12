package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.exception.WichtelnMailCreationException;
import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Donor;
import com.rmnbhm.wichteln.model.ParticipantsMatch.Recipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Component
public class WichtelnMailCreator {

    public static final String MAIL_TXT = "mail.txt";
    @Autowired
    private final TemplateEngine templateEngine;
    @Autowired
    private final JavaMailSender mailSender;

    public WichtelnMailCreator(TemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public MimeMessage createMessage(Event event, ParticipantsMatch match) {
        try {
            return createMimeMessage(event, match.getDonor(), match.getRecipient());
        } catch (MessagingException e) {
            // Re-throw as custom `RuntimeException` to be handled by upstream by `ErrorController`
            throw new WichtelnMailCreationException();
        }
    }

    private MimeMessage createMimeMessage(Event event, Donor donor, Recipient recipient) throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable("event", event);
        ctx.setVariable("donor", donor.getName());
        ctx.setVariable("recipient", recipient.getName());

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
        message.setSubject(String.format("You have been invited to wichtel at %s", event.getTitle()));
        message.setFrom("wichteln@romanboehm.com");
        message.setTo(donor.getEmail());

        String textContent = templateEngine.process(MAIL_TXT, ctx);
        message.setText(textContent);

        return mimeMessage;
    }

}
