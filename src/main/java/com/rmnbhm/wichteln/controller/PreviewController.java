package com.rmnbhm.wichteln.controller;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.service.WichtelnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "preview")
public class PreviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewController.class);
    private static final Collection<CurrencyUnit> CURRENCIES = Monetary.getCurrencies();
    private final WichtelnService wichtelnService;

    public PreviewController(WichtelnService wichtelnService) {
        this.wichtelnService = wichtelnService;
    }

    @GetMapping
    public ModelAndView getPreview(@ModelAttribute @Valid Event event) {
        SimpleMailMessage mailPreview = wichtelnService.createPreview(event);
        LOGGER.debug("Previewed {}", event);
        return new ModelAndView(
                "preview",
                Map.of(
                        "preview", mailPreview,
                        "currencies", CURRENCIES
                ),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ModelAndView saveEvent(@ModelAttribute @Valid Event event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.debug(
                    "Failed to create {} because {}",
                    event,
                    bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(", "))
            );
            return new ModelAndView("wichteln", Map.of("currencies", CURRENCIES), HttpStatus.BAD_REQUEST);
        }
        wichtelnService.save(event);
        LOGGER.debug("Saved {}", event);
        return new ModelAndView(new RedirectView("wichteln"));
    }
}
