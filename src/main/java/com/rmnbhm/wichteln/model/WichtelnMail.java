package com.rmnbhm.wichteln.model;

import com.rmnbhm.wichteln.exception.IllegalWichtelnMailStateException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

public class WichtelnMail {

    private final MimeMessage mimeMessage;

    public WichtelnMail(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    public String from() {
        try {
            return mimeMessage.getFrom()[0].toString();
        } catch (MessagingException e) {
            throw new IllegalWichtelnMailStateException("from");
        }
    }

    public String to() {
        try {
            return mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString();
        } catch (MessagingException e) {
            throw new IllegalWichtelnMailStateException("to");
        }
    }

    public String subject() {
        try {
            return mimeMessage.getSubject();
        } catch (MessagingException e) {
            throw new IllegalWichtelnMailStateException("subject");
        }
    }

    public String body() {
        try {
            return mimeMessage.getContent().toString();
        } catch (IOException | MessagingException e) {
            throw new IllegalWichtelnMailStateException("body");
        }
    }

    public MimeMessage getMimeMessage() {
        return mimeMessage;
    }

    @Override
    public String toString() {
        return String.format("WichtelnMail(from=%s, to=%s, subject=%s, body=%s)", from(), to(), subject(), body());
    }
}
