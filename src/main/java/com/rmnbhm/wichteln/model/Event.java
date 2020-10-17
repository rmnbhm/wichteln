package com.rmnbhm.wichteln.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Event {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    @Min(0)
    private Integer monetaryAmount;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @FutureOrPresent
    private Date heldAt;

    @Singular
    @NotNull
    @Size(min = 3, max = 100)
    private List<@Valid Participant> participants;

    public void addParticipant(Participant participant) {
        participants = new ArrayList<>(participants); // Needed since Lombok's `@Builder` creates an unmodifiable list
        participants.add(participant);
    }

    private void removeParticipant(Participant participant) {
        participants = new ArrayList<>(participants); // Needed since Lombok's `@Builder` creates an unmodifiable list
        participants.remove(participant);
    }

    public void removeParticipantNumber(int index) {
        removeParticipant(participants.get(index));
    }
}