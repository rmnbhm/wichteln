package com.rmnbhm.wichteln.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Event {
    private String title;
    private String description;
    private Integer monetaryAmount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date heldAt;
    private List<Participant> participants = new ArrayList<>();

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    private void removeParticipant(Participant participant) {
        participants.remove(participant);
    }

    public void removeParticipantNumber(int index) {
        removeParticipant(participants.get(index));
    }
}
