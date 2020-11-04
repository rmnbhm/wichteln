package com.rmnbhm.wichteln.controller;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.service.WichtelnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class WichtelnController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WichtelnController.class);
    private static final Collection<CurrencyUnit> CURRENCIES = Monetary.getCurrencies();
    private static final String WICHTELN_VIEW = "wichteln";
    private final WichtelnService wichtelnService;

    public WichtelnController(WichtelnService wichtelnService) {
        this.wichtelnService = wichtelnService;
    }

    @GetMapping
    public String getEvent(Model model) {
        Event event = new Event();
        // at least three participants needed
        event.addParticipant(new Participant());
        event.addParticipant(new Participant());
        event.addParticipant(new Participant());
        Event.MonetaryAmount monetaryAmount = new Event.MonetaryAmount();
        monetaryAmount.setCurrency(Monetary.getCurrency("EUR")); // set default currency
        event.setMonetaryAmount(monetaryAmount);
        model.addAttribute("event", event);
        model.addAttribute("currencies", CURRENCIES);
        return "wichteln";
    }

    @PostMapping
    public ModelAndView saveEvent(@ModelAttribute @Valid Event event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.debug(
                    "Failed to save {} because {}",
                    event,
                    bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(", "))
            );
            return new ModelAndView(WICHTELN_VIEW, Map.of("currencies", CURRENCIES), HttpStatus.BAD_REQUEST);
        }
        wichtelnService.save(event);
        LOGGER.debug("Saved {}", event);
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/add")
    public ModelAndView addParticipant(@ModelAttribute Event event) {
        event.addParticipant(new Participant());
        LOGGER.debug("Added participant to {}", event);
        return new ModelAndView(
                WICHTELN_VIEW,
                Map.of(
                        "event", event,
                        "currencies", CURRENCIES
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/remove/{index}")
    public ModelAndView removeParticipant(
            @PathVariable(name = "index") Integer participantIndex,
            @ModelAttribute Event event
    ) {
        event.removeParticipantNumber(participantIndex);
        LOGGER.debug("Removed participant {} from {}", participantIndex, event);

        return new ModelAndView(
                WICHTELN_VIEW,
                Map.of(
                        "event", event,
                        "currencies", CURRENCIES
                ),
                HttpStatus.OK
        );
    }
}
