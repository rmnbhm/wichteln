package com.rmnbhm.wichteln.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.money.CurrencyUnit;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
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
                // Since `Event#description` backs an `input[@type="textarea"]` element which allows for line breaks
                // we make sure we prevent
                // - line breaks
                // - too long text
                // in logs.
                StringUtils.abbreviate(StringEscapeUtils.escapeJava(this.getDescription()), 50),
                this.getMonetaryAmount(),
                this.getLocalDateTime(),
                this.getPlace(),
                this.getHost(),
                this.getParticipants()
        );
    }

    public static class Host {

        @NotBlank
        @Size(max = 100)
        private String name;

        @NotBlank
        @Email
        private String email;

        public Host() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String toString() {
            return String.format("Event.Host(name=%s, email=%s)", this.getName(), this.getEmail());
        }
    }

    public static class MonetaryAmount {

        @NotNull
        @Min(0)
        private BigDecimal number;

        @NotNull
        private CurrencyUnit currency;

        public MonetaryAmount() {
        }

        public BigDecimal getNumber() {
            return number;
        }

        public void setNumber(BigDecimal number) {
            this.number = number;
        }

        public CurrencyUnit getCurrency() {
            return currency;
        }

        public void setCurrency(CurrencyUnit currency) {
            this.currency = currency;
        }

        public String toString() {
            return String.format("Event.MonetaryAmount(number=%s, currency=%s)", this.getNumber(), this.getCurrency());
        }
    }
}
