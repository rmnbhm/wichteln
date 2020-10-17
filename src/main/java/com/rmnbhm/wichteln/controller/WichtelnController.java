package com.rmnbhm.wichteln.controller;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Participant;
import com.rmnbhm.wichteln.service.WichtelnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class WichtelnController {

    private final WichtelnService wichtelnService;

    @GetMapping
    public String getEvent(Model model) {
        Event event = Event.builder()
                // at least three participants needed
                .participant(Participant.builder().build())
                .participant(Participant.builder().build())
                .participant(Participant.builder().build())
                .build();
        model.addAttribute("event", event);
        return "wichteln";
    }

    @PostMapping
    public ModelAndView saveEvent(@ModelAttribute @Valid Event event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("wichteln", HttpStatus.BAD_REQUEST);
        }
        wichtelnService.save(event);
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/add")
    public String addParticipant(@ModelAttribute Event event, Model model) {
        event.addParticipant(Participant.builder().build());
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
