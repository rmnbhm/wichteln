package com.rmnbhm.wichteln.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.Future;

@Component
public class WichtelnMailDispatcher {
    private final JavaMailSender javaMailSender;

    public WichtelnMailDispatcher(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public Future<Void> send(MimeMessage mail) {
        javaMailSender.send(mail);
        return null; // Needed because of method return type
    }
}
