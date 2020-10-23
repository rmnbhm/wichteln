package com.rmnbhm.wichteln.controller;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.service.WichtelnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.spi.DefaultMonetaryCurrenciesSingletonSpi;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WichtelnController {

    private static final Collection<CurrencyUnit> CURRENCY_UNITS = Monetary.getCurrencies();
    private final WichtelnService wichtelnService;

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
        model.addAttribute("currencies", CURRENCY_UNITS);
        return "wichteln";
    }

    @PostMapping
    public ModelAndView saveEvent(@ModelAttribute @Valid Event event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("wichteln", Map.of("currencies", CURRENCY_UNITS), HttpStatus.BAD_REQUEST);
        }
        wichtelnService.save(event);
        log.debug("Saved {}", event);
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/add")
    public String addParticipant(@ModelAttribute Event event, Model model) {
        event.addParticipant(new Participant());
        model.addAttribute("event", event);
        log.debug("Added participant to {}", event);

        return "wichteln";
    }

    @PostMapping("/remove/{index}")
    public String removeParticipant(
            @PathVariable(name = "index") Integer participantIndex,
            @ModelAttribute Event event,
            Model model
    ) {
        event.removeParticipantNumber(participantIndex);
        model.addAttribute("event", event);
        log.debug("Removed participant {} from {}", participantIndex, event);

        return "wichteln";
    }
}
