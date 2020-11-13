package com.rmnbhm.wichteln.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.rmnbhm.wichteln.config.WebConfig.PRIVACY_VIEW;

@Controller
@RequestMapping(path = "privacy")
public class PrivacyController {

    @GetMapping
    public String getAbout() {
        return PRIVACY_VIEW;
    }
}
