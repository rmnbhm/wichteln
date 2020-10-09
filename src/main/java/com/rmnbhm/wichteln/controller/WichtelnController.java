package com.rmnbhm.wichteln.controller;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.service.WichtelnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequiredArgsConstructor
public class WichtelnController {

    private final WichtelnService wichtelnService;

    @GetMapping
    public String getEvent(Model model) {
        Event event = new Event();
        // at least three participants needed
        event.addParticipant(new Participant());
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

    @PostMapping("/add")
    public String addParticipant(@ModelAttribute Event event, Model model) {
        event.addParticipant(new Participant());
        model.addAttribute("event", event);

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

        return "wichteln";
    }
}
