package com.rmnbhm.wichteln.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.rmnbhm.wichteln.TestData;
import com.rmnbhm.wichteln.exception.WichtelnMailCreationException;
import com.rmnbhm.wichteln.service.WichtelnMailCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.FlashMap;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=testuser",
        "spring.mail.password=testpassword",
        "spring.mail.protocol=smtp"
})
@AutoConfigureMockMvc
public class PreviewControllerTest {

    private static GreenMail greenMail;

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private WichtelnMailCreator wichtelnMailCreator;

    @SpyBean
    private JavaMailSender mailSender;

    @BeforeAll
    public static void setupGreenmail() {
        greenMail = new GreenMail(ServerSetupTest.ALL);
        greenMail.withConfiguration(
                GreenMailConfiguration
                        .aConfig()
                        .withDisabledAuthentication()
        );
        greenMail.start();
    }

    @AfterEach
    public void cleanup() throws FolderException {
        if (greenMail != null) {
            greenMail.purgeEmailFromAllMailboxes();
        }
    }


    @Test
    public void shouldInform() throws Exception {
        mockMvc.perform(
                post("/preview")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(TestData.event().asFormParams())

        )
                .andExpect(status().is3xxRedirection());


        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .containsExactlyInAnyOrder(
                        "angusyoung@acdc.net",
                        "malcolmyoung@acdc.net",
                        "philrudd@acdc.net"
                );
    }

    @Test
    public void shouldStillValidateToPreventHiddenFormTampering() throws Exception {
        LocalDateTime invalidDateTime = Instant.now().minus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .toLocalDateTime();

        mockMvc.perform(
                post("/preview")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(
                                TestData.event()
                                        .modifying(event -> event.setLocalDateTime(invalidDateTime))
                                        .asFormParams()
                        )
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Must take place in the future.")));

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isFalse();
    }

    @Test
    public void shouldPreview() throws Exception {
        LocalDateTime localDateTime = Instant.now()
                .plus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .toLocalDateTime();

        FlashMap flashMap = mockMvc.perform(post("/wichteln")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("preview"))
                .andExpect(flash().attributeCount(1))
                .andReturn().getFlashMap();

        mockMvc.perform(get("/preview").flashAttrs(flashMap))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString(
                "<p>Hey <span>Angus Young</span>,</p>" +
                        "<p>You have been invited to wichtel at <span>AC/DC Secret Santa</span> (<a href=\"https://wichteln.rmnbhm.com/about\">https://wichteln.rmnbhm.com/about</a>)!<br/>" +
                        "You're therefore asked to give a gift to <span>Phil Rudd</span>. The gift's monetary value should not exceed <span>AUD 78.50</span>.<br/>" +
                        String.format(
                                "The event will take place at <span>Sydney Harbor</span> on <span>%s</span> at <span>%s</span> local time.</p>",
                                LocalDate.from(localDateTime),
                                LocalTime.from(localDateTime).truncatedTo(ChronoUnit.MINUTES)
                        ) +
                        "The event will take place at <span>Sydney Harbor</span> on <span>2020-12-06</span> at <span>20:Sydney Harbor33</span> local time.</p>" +
                        "<p>Here's what the event's host says about it:</p>" +
                        "<p>\"<span>There&#39;s gonna be some santa&#39;ing</span>\"</p>" +
                        "<p>If you have any questions, contact the event's host <span>George Young</span> at <a href=\"mailto:georgeyoung@acdc.net\"><span>georgeyoung@acdc.net</span></a>.</p>" +
                        "<p>This mail was generated using <a href=\"https://wichteln.rmnbhm.com\">https://wichteln.rmnbhm.com</a></p>"
                )));
    }

    @Test
    public void shouldShowErrorPageWhenMailCannotBeSent() throws Exception {
        Mockito.doThrow(new MailException("error") {
        }).when(mailSender).send(any(MimeMessage.class));

        FlashMap flashMap = mockMvc.perform(post("/wichteln")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("preview"))
                .andExpect(flash().attributeCount(1))
                .andReturn().getFlashMap();

        mockMvc.perform(get("/preview").flashAttrs(flashMap))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/preview")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(status().is5xxServerError())
                .andExpect(view().name("error"));
    }
}