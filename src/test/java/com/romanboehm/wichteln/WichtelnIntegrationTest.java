package com.romanboehm.wichteln;

import javax.mail.Address;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class WichtelnIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication());

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private JavaMailSender mailSender;

    @Test
    public void shouldDoGetFormPreviewMailSendMailFlow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wichteln"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/preview")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.stringContainsInOrder(
                        "Hey <span>Angus Young</span>,",
                        "You have been invited to wichtel at <span>&#39;AC/DC Secret Santa&#39;</span> (<a href=\"https://wichteln.romanboehm.com/about\">https://wichteln.romanboehm.com/about</a>)!<br/>",
                        "You're therefore asked to give a gift to <span>Phil Rudd</span>. The gift's monetary value should not exceed <span>AUD 78.50</span>.<br/>",
                        "The event will take place at <span>Sydney Harbor</span> on <span>2666-06-07</span> at <span>06:06</span> local time.",
                        "Here's what the event's host says about it:",
                        "<i>\"<span>There&#39;s gonna be some santa&#39;ing</span>\"</i>",
                        "If you have any questions, contact the event's host <span>George Young</span> at <a href=\"mailto:georgeyoung@acdc.net\"><span>georgeyoung@acdc.net</span></a>.",
                        "This mail was generated using <a href=\"https://wichteln.romanboehm.com\">https://wichteln.romanboehm.com</a>"
                )));

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        Assertions.assertThat(greenMail.waitForIncomingEmail(1500, 4)).isTrue();
        Assertions.assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .containsExactlyInAnyOrder(
                        "angusyoung@acdc.net",
                        "malcolmyoung@acdc.net",
                        "philrudd@acdc.net",
                        "georgeyoung@acdc.net"
                );
    }
}