package com.romanboehm.wichteln.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.romanboehm.wichteln.TestData;
import com.romanboehm.wichteln.service.WichtelnService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
public class WichtelnControllerTest {

    @MockBean
    private WichtelnService wichtelnService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldValidate() throws Exception {
        LocalDate invalidDate = LocalDate.now().minus(1, ChronoUnit.DAYS);

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/preview")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(
                        TestData
                                .event()
                                .modifying(event -> event.setLocalDate(invalidDate))
                                .asFormParams()
                )
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(
                        "Must take place in the future."
                )));
    }
}