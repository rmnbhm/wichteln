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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.mail.Address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WichtelnControllerTest {

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
        mockMvc.perform(
                post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "My+Title")
                        .param("description", "My+Description")
                        .param("monetaryAmount", "78")
                        .param("heldAt", "2020-09-17")
                        .param("participants[0].firstName", "Angus")
                        .param("participants[0].lastName", "Young")
                        .param("participants[0].email", "angusyoung@acdc.net")
                        .param("participants[1].firstName", "Malcolm")
                        .param("participants[1].lastName", "Young")
                        .param("participants[1].email", "malcolmyoung@acdc.net")
                        .param("participants[2].firstName", "Phil")
                        .param("participants[2].lastName", "Rudd")
                        .param("participants[2].email", "philrudd@acdc.net")

        ).andExpect(status().is3xxRedirection());


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
}