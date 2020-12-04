package com.rmnbhm.wichteln.model;


import com.rmnbhm.wichteln.exception.IllegalWichtelnMailStateException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
public class WichtelnMailTest {

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private WichtelnMail wichtelnMail;

    @Test
    public void throwsWhenMimeMessageInIllegalState() throws MessagingException {
        Mockito.when(mimeMessage.getFrom()).thenThrow(MessagingException.class);
        Assertions.assertThatThrownBy(() -> wichtelnMail.from())
                .isInstanceOf(IllegalWichtelnMailStateException.class)
                .hasMessageContaining("Could not retrieve 'from' from");
    }
}