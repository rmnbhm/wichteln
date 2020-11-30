package com.rmnbhm.wichteln.controller;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.service.WichtelnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rmnbhm.wichteln.config.WebConfig.PREVIEW_VIEW;
import static com.rmnbhm.wichteln.config.WebConfig.WICHTELN_VIEW;

@Controller
@RequestMapping(path = {"/", "/wichteln"})
public class WichtelnController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WichtelnController.class);
    private final WichtelnService wichtelnService;

    public WichtelnController(WichtelnService wichtelnService) {
        this.wichtelnService = wichtelnService;
    }

    @GetMapping
    public ModelAndView getEvent() {
        return new ModelAndView(WICHTELN_VIEW, Map.of("event", Event.withMinimalDefaults()), HttpStatus.OK);
    }

    @PostMapping
    public ModelAndView previewEvent(
            @ModelAttribute @Valid Event event,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            LOGGER.debug(
                    "Failed to create {} because {}",
                    event,
                    bindingResult.getAllErrors()
                            .stream()
                            .map(ObjectError::toString)
                            .collect(Collectors.joining(", "))
            );
            return new ModelAndView(WICHTELN_VIEW, HttpStatus.BAD_REQUEST);
        }
        redirectAttributes.addFlashAttribute("event", event);
        return new ModelAndView(new RedirectView(PREVIEW_VIEW));
    }

    @PostMapping("/add")
    public ModelAndView addParticipant(@ModelAttribute Event event) {
        event.addParticipant(new Participant());
        LOGGER.debug("Added participant to {}", event);
        return new ModelAndView(WICHTELN_VIEW, HttpStatus.OK);
    }

    @PostMapping("/remove/{index}")
    public ModelAndView removeParticipant(
            @PathVariable(name = "index") Integer participantIndex,
            @ModelAttribute Event event
    ) {
        event.removeParticipantNumber(participantIndex);
        LOGGER.debug("Removed participant {} from {}", participantIndex, event);

        return new ModelAndView(WICHTELN_VIEW, HttpStatus.OK);
    }
}
