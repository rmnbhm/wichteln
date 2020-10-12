package com.rmnbhm.wichteln.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.mail.Address;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=testuser",
        "spring.mail.password=testpassword",
        "spring.mail.protocol=smtp"
})
@AutoConfigureMockMvc
public class WichtelnControllerTest {

    private static GreenMail greenMail;

    @Autowired
    private MockMvc mockMvc;

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
        String dateTime = Instant.now().plus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        mockMvc.perform(
                post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "AC/DC Secret Santa")
                        .param("description", "There's gonna be some santa'ing")
                        .param("monetaryAmount", "78")
                        .param("heldAt", dateTime)
                        .param("participants[0].firstName", "Angus")
                        .param("participants[0].lastName", "Young")
                        .param("participants[0].email", "angusyoung@acdc.net")
                        .param("participants[1].firstName", "Malcolm")
                        .param("participants[1].lastName", "Young")
                        .param("participants[1].email", "malcolmyoung@acdc.net")
                        .param("participants[2].firstName", "Phil")
                        .param("participants[2].lastName", "Rudd")
                        .param("participants[2].email", "philrudd@acdc.net")

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
        String invalidDateTime = Instant.now().minus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        mockMvc.perform(
                post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "AC/DC Secret Santa")
                        .param("description", "There's gonna be some santa'ing")
                        .param("monetaryAmount", "78")
                        .param("heldAt", invalidDateTime)
                        .param("participants[0].firstName", "Angus")
                        .param("participants[0].lastName", "Young")
                        .param("participants[0].email", "angusyoung@acdc.net")
                        .param("participants[1].firstName", "Malcolm")
                        .param("participants[1].lastName", "Young")
                        .param("participants[1].email", "malcolmyoung@acdc.net")
                        .param("participants[2].firstName", "Phil")
                        .param("participants[2].lastName", "Rudd")
                        .param("participants[2].email", "philrudd@acdc.net")

        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("must be a date in the present or in the future")));

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isFalse();
    }
}