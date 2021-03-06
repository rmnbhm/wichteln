package com.romanboehm.wichteln.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.romanboehm.wichteln.TestData;
import com.romanboehm.wichteln.TestUtils;
import com.romanboehm.wichteln.model.Event;
import com.romanboehm.wichteln.model.Participant;
import com.romanboehm.wichteln.model.ParticipantsMatch;
import com.romanboehm.wichteln.model.SendResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

@SpringBootTest
public class WichtelnMailerTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication());

    @Autowired
    private WichtelnMailer wichtelnMailer;

    @SpyBean
    private JavaMailSender occasionallyFailingSender;

    @SpyBean
    private AsyncHostMailer hostMailer;

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTreatMailProblemAsNegativeSendResult() {
        Event event = TestData.event().asObject();
        Participant angusYoung = event.getParticipants().get(0);
        Participant malcolmYoung = event.getParticipants().get(1);
        Participant philRudd = event.getParticipants().get(2);

        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new ParticipantsMatch.Donor(angusYoung), new ParticipantsMatch.Recipient(malcolmYoung)
        );
        ParticipantsMatch malcolmGiftsToPhil = new ParticipantsMatch(
                new ParticipantsMatch.Donor(malcolmYoung), new ParticipantsMatch.Recipient(philRudd)
        );
        ParticipantsMatch philGiftsToAngus = new ParticipantsMatch(
                new ParticipantsMatch.Donor(philRudd), new ParticipantsMatch.Recipient(angusYoung)
        );

        // One of three emails for participants fails.
        Mockito
                .doThrow(new MailSendException("error"))
                .when(occasionallyFailingSender).send(ArgumentMatchers.argThat(
                        TestUtils.isSentTo(angusYoung.getEmail()))
        );
        Mockito
                .doCallRealMethod()
                .when(occasionallyFailingSender).send(ArgumentMatchers.argThat(
                        TestUtils.isSentTo(malcolmYoung.getEmail()))
        );
        Mockito
                .doCallRealMethod()
                .when(occasionallyFailingSender).send(ArgumentMatchers.argThat(
                        TestUtils.isSentTo(philRudd.getEmail()))
        );

        // Email to host is sent successfully
        Mockito
                .doCallRealMethod()
                .when(occasionallyFailingSender).send(ArgumentMatchers.argThat(
                        TestUtils.isSentTo(event.getHost().getEmail()))
        );

        wichtelnMailer.send(event, List.of(angusGiftsToMalcolm, malcolmGiftsToPhil, philGiftsToAngus));

        ArgumentCaptor<List<SendResult>> capturedSendResults = ArgumentCaptor.forClass(List.class);
        Mockito.verify(hostMailer).send(ArgumentMatchers.any(), capturedSendResults.capture());
        Assertions.assertThat(capturedSendResults.getValue())
                .usingFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        SendResult.failure("Angus Young", "angusyoung@acdc.net"),
                        SendResult.success("Malcolm Young", "malcolmyoung@acdc.net"),
                        SendResult.success("Phil Rudd", "philrudd@acdc.net")
                );
    }

}