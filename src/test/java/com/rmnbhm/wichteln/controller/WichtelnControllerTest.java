package com.rmnbhm.wichteln.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldValidate() throws Exception {
        String invalidDateTime = Instant.now().minus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        mockMvc.perform(
                post("/wichteln")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "AC/DC Secret Santa")
                        .param("description", "There's gonna be some santa'ing")
                        .param("monetaryAmount.number", "78.50")
                        .param("monetaryAmount.currency", "AUD")
                        .param("localDateTime", invalidDateTime)
                        .param("place", "Sydney")
                        .param("host.name", "George Young")
                        .param("host.email", "georgeyoung@acdc.net")
                        .param("participants[0].name", "Angus Young")
                        .param("participants[0].email", "angusyoung@acdc.net")
                        .param("participants[1].name", "Malcolm Young")
                        .param("participants[1].email", "malcolmyoung@acdc.net")
                        .param("participants[2].name", "Phil Young")
                        .param("participants[2].email", "philrudd@acdc.net")

        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Must take place in the future.")));
    }
}