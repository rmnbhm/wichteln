package com.rmnbhm.wichteln.controller;

import com.rmnbhm.wichteln.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.FlashMap;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
        LocalDateTime invalidDateTime = Instant.now().minus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .toLocalDateTime();

        mockMvc.perform(
                post("/wichteln")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(
                                TestData
                                        .event()
                                        .modifying(event -> event.setLocalDateTime(invalidDateTime))
                                        .asFormParams()
                        )
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Must take place in the future.")));
    }

    @Test
    public void shouldRedirectToPreview() throws Exception {
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
    }
}