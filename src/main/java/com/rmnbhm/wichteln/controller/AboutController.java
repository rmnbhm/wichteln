package com.rmnbhm.wichteln.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "about")
public class AboutController {

    private static final String ABOUT_VIEW = "about";

    @GetMapping
    public String getAbout() {
        return ABOUT_VIEW;
    }
}
