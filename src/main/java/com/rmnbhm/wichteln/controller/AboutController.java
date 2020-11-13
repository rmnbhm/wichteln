package com.rmnbhm.wichteln.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.rmnbhm.wichteln.config.WebConfig.ABOUT_VIEW;

@Controller
@RequestMapping(path = "about")
public class AboutController {

    @GetMapping
    public String getAbout() {
        return ABOUT_VIEW;
    }
}
