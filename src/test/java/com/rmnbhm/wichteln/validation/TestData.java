package com.rmnbhm.wichteln.validation;

import com.rmnbhm.wichteln.model.Event;
import com.rmnbhm.wichteln.model.Host;
import com.rmnbhm.wichteln.model.MonetaryAmount;
import com.rmnbhm.wichteln.model.Participant;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TestData {

    public static Event event() {
        Event acdcSanta = new Event();
        acdcSanta.setTitle("AC/DC Secret Santa");
        acdcSanta.setDescription("There's gonna be some santa'ing");
        acdcSanta.setMonetaryAmount(monetaryAmount());
        acdcSanta.setLocalDateTime(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        acdcSanta.setPlace("Sydney");
        acdcSanta.setHost(host());
        Participant angusYoung = new Participant();
        angusYoung.setName("Angus Young");
        angusYoung.setEmail("angusyoung@acdc.net");
        Participant malcolmYoung = new Participant();
        malcolmYoung.setName("Malcolm Young");
        malcolmYoung.setEmail("malcolmyoung@acdc.net");
        Participant philRudd = new Participant();
        philRudd.setName("Phil Rudd");
        philRudd.setEmail("philrudd@acdc.net");
        acdcSanta.setParticipants(List.of(angusYoung, malcolmYoung, philRudd));
        return acdcSanta;
    }

    public static Host host() {
        Host georgeYoung = new Host();
        georgeYoung.setName("George Young");
        georgeYoung.setEmail("angusyoung@acdc.net");
        return georgeYoung;
    }

    public static MonetaryAmount monetaryAmount() {
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        monetaryAmount.setCurrency(Monetary.getCurrency("AUD"));
        monetaryAmount.setNumber(BigDecimal.valueOf(78.50));
        return monetaryAmount;
    }

    public static Participant participant() {
        Participant angusYoung = new Participant();
        angusYoung.setName("Angus Young");
        angusYoung.setEmail("angusyoung@acdc.net");
        return angusYoung;
    }
}
