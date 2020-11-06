package com.rmnbhm.wichteln.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "privacy")
public class PrivacyController {

    private static final String PRIVACY_VIEW = "privacy";

    @GetMapping
    public String getAbout() {
        return PRIVACY_VIEW;
    }
}
