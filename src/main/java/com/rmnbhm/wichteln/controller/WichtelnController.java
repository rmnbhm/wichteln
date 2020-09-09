package com.rmnbhm.wichteln.controller;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.service.WichtelnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WichtelnController {

    private final WichtelnService wichtelnService;

    @Autowired
    public WichtelnController(WichtelnService wichtelnService) {
        this.wichtelnService = wichtelnService;
    }

    @GetMapping
    public String getEvent(Model model) {
        Event event = new Event();
        // at least two participants needed
        event.addParticipant(new Participant());
        event.addParticipant(new Participant());
        model.addAttribute("event", event);
        return "wichteln";
    }

    @PostMapping
    public String saveEvent(@ModelAttribute Event event, Model model) {
        wichtelnService.save(event);
        
        return "redirect:/";
    }

    @PostMapping(params = {"add-participant"})
    public String addParticipant(@ModelAttribute Event event, Model model) {
        event.addParticipant(new Participant());
        model.addAttribute("event", event);

        return "wichteln";
    }

    @PostMapping(params = {"remove-participant"})
    public String removeParticipant(
            @RequestParam(name = "remove-participant") Integer participantIndex,
            @ModelAttribute Event event,
            Model model
    ) {
        event.removeParticipantNumber(participantIndex);
        model.addAttribute("event", event);

        return "wichteln";
    }
}
