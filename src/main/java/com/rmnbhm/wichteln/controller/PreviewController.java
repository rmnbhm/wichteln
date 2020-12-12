package com.rmnbhm.wichteln.controller;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.service.WichtelnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "preview")
public class PreviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewController.class);
    private final WichtelnService wichtelnService;

    public PreviewController(WichtelnService wichtelnService) {
        this.wichtelnService = wichtelnService;
    }

    @GetMapping
    public ModelAndView getPreview(@ModelAttribute @Valid Event event) {
        LOGGER.info("Previewed {}", event);
        return new ModelAndView("preview", HttpStatus.OK);
    }

    @PostMapping
    public ModelAndView saveEvent(@ModelAttribute @Valid Event event) {
        wichtelnService.save(event);
        LOGGER.info("Saved {}", event);
        return new ModelAndView(new RedirectView("wichteln"));
    }
}
