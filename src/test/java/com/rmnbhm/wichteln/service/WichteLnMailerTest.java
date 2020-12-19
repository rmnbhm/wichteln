package com.rmnbhm.wichteln.service;

import com.rmnbhm.wichteln.TestData;
import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.model.ParticipantsMatch;
import com.rmnbhm.wichteln.model.SendResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.argThat;

@SpringBootTest(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=testuser",
        "spring.mail.password=testpassword",
        "spring.mail.protocol=smtp"
})
public class WichteLnMailerTest {

    @Autowired
    private WichtelnMailer mailSender;

    @SpyBean
    private JavaMailSender timingOutInternalMailSender;


    @Test
    public void shouldTreatMailTimeoutAsNegativeSendResult() {
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
        ParticipantsMatch PhilGiftsToAngus = new ParticipantsMatch(
                new ParticipantsMatch.Donor(philRudd), new ParticipantsMatch.Recipient(angusYoung)
        );
        CountDownLatch latch = new CountDownLatch(1);

        ArgumentMatcher<MimeMessage> isSentToMalcolm = argument -> {
            try {
                String emailRecipient = argument.getRecipients(Message.RecipientType.TO)[0].toString();
                return malcolmYoung.getEmail().equals(emailRecipient);
            } catch (MessagingException e) {
                return false;
            }
        };

        // Email for match two fails with timeout.
        Mockito.doAnswer(invocation -> {
            Thread.sleep(5000);
            latch.countDown();
            return null;
        })
                .when(timingOutInternalMailSender)
                .send(argThat(isSentToMalcolm));

        // Emails for matches one and three are successful.
        Mockito.doAnswer(invocation -> null)
                .when(timingOutInternalMailSender)
                .send(not(argThat(isSentToMalcolm)));

        assertThat(mailSender.send(event, angusGiftsToMalcolm)).extracting(SendResult::isSuccess).isEqualTo(true);
        assertThat(mailSender.send(event, malcolmGiftsToPhil)).extracting(SendResult::isSuccess).isEqualTo(false);
        assertThat(mailSender.send(event, PhilGiftsToAngus)).extracting(SendResult::isSuccess).isEqualTo(true);
    }

}