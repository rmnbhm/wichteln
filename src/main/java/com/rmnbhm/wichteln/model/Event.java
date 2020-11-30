package com.rmnbhm.wichteln.model;

import com.rmnbhm.wichteln.validation.NoHtml;
import com.rmnbhm.wichteln.validation.NoJavaControlCharacters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.money.Monetary;
import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {

    public static Event withMinimalDefaults() {
        Event event = new Event();
        // at least three participants needed
        event.addParticipant(new Participant());
        event.addParticipant(new Participant());
        event.addParticipant(new Participant());
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        monetaryAmount.setCurrency(Monetary.getCurrency("EUR")); // set default currency
        event.setMonetaryAmount(monetaryAmount);
        return event;
    }

    @NotBlank
    @Size(max = 100)
    @NoJavaControlCharacters
    @NoHtml
    private String title;

    @NotBlank
    @Size(max = 1000)
    @NoJavaControlCharacters
    @NoHtml
    private String description;

    @NotNull
    @Valid
    private MonetaryAmount monetaryAmount;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @FutureOrPresent
    private LocalDateTime localDateTime;

    @NotBlank
    @Size(max = 100)
    @NoJavaControlCharacters
    @NoHtml
    private String place;

    @NotNull
    @Valid
    private Host host;

    @NotNull
    @Size(min = 3, max = 100)
    private List<@Valid Participant> participants;

    public Event() {
        participants = new ArrayList<>();
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    private void removeParticipant(Participant participant) {
        participants.remove(participant);
    }

    public void removeParticipantNumber(int index) {
        removeParticipant(participants.get(index));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MonetaryAmount getMonetaryAmount() {
        return monetaryAmount;
    }

    public void setMonetaryAmount(MonetaryAmount monetaryAmount) {
        this.monetaryAmount = monetaryAmount;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public String toString() {
        return String.format(
                "Event(title=%s, description=%s, monetaryAmount=%s, localDateTime=%s, place=%s, host=%s, participants=%s)",
                this.getTitle(),
                // Make sure text is not too long since `Event#description` allows for up to 1000 characters.
                StringUtils.abbreviate(this.getDescription(), 50),
                this.getMonetaryAmount(),
                this.getLocalDateTime(),
                this.getPlace(),
                this.getHost(),
                this.getParticipants()
        );
    }

}
