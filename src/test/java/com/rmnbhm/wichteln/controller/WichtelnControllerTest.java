package com.rmnbhm.wichteln.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.rmnbhm.wichteln.TestData;
import com.rmnbhm.wichteln.service.WichtelnMailCreator;
import org.hamcrest.Matchers;
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

import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=testuser",
        "spring.mail.password=testpassword",
        "spring.mail.protocol=smtp"
})
@AutoConfigureMockMvc
public class WichtelnControllerTest {

    private static final String[] PREVIEW_HTML = new String[]{
            "Hey <span>Angus Young</span>,",
            "You have been invited to wichtel at <span>AC/DC Secret Santa</span> (<a href=\"https://wichteln.rmnbhm.com/about\">https://wichteln.rmnbhm.com/about</a>)!<br/>",
            "You're therefore asked to give a gift to <span>Phil Rudd</span>. The gift's monetary value should not exceed <span>AUD 78.50</span>.<br/>",
            "The event will take place at <span>Sydney Harbor</span> on <span>2666-06-06</span> at <span>06:06</span> local time.",
            "Here's what the event's host says about it:",
            "<i>\"<span>There&#39;s gonna be some santa&#39;ing</span>\"</i>",
            "If you have any questions, contact the event's host <span>George Young</span> at <a href=\"mailto:georgeyoung@acdc.net\"><span>georgeyoung@acdc.net</span></a>.",
            "This mail was generated using <a href=\"https://wichteln.rmnbhm.com\">https://wichteln.rmnbhm.com</a>"
    };

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
    public void shouldDoGetFormPreviewMailSendMailFlow() throws Exception {
        mockMvc.perform(get("/wichteln"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/wichteln/preview")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(Matchers.stringContainsInOrder(PREVIEW_HTML)));

        mockMvc.perform(post("/wichteln/save")
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
    public void shouldValidate() throws Exception {
        LocalDate invalidDate = LocalDate.now().minus(1, ChronoUnit.DAYS);

        mockMvc.perform(post("/wichteln/preview")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(
                        TestData
                                .event()
                                .modifying(event -> event.setLocalDate(invalidDate))
                                .asFormParams()
                )
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Must take place in the future.")));
    }

    @Test
    public void shouldStillValidateInPreviewModeToPreventTamperingWithHiddenForm() throws Exception {
        mockMvc.perform(post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(
                        TestData.event()
                                .modifying(event -> event.setTitle("This title is too long".repeat(20)))
                                .asFormParams()
                )
        );

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isFalse();
    }

    @Test
    public void shouldShowErrorPageWhenMailCannotBeSent() throws Exception {
        Mockito.doThrow(new MailException("error") {
        }).when(mailSender).send(any(MimeMessage.class));

        mockMvc.perform(post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(status().is5xxServerError())
                .andExpect(view().name("error"));

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isFalse();
    }
}