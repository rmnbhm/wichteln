package com.rmnbhm.wichteln.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Delegate;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.spi.DefaultMonetaryCurrenciesSingletonSpi;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.springframework.format.annotation.DateTimeFormat;

import javax.money.CurrencyUnit;
import javax.money.NumberValue;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
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
    private LocalDateTime heldAt;

    @NotNull
    @Valid
    private Host host;

    @NotNull
    @Size(min = 3, max = 100)
    private List<@Valid Participant> participants = new ArrayList<>();

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    private void removeParticipant(Participant participant) {
        participants.remove(participant);
    }

    public void removeParticipantNumber(int index) {
        removeParticipant(participants.get(index));
    }

    @Data
    @NoArgsConstructor
    public static class Host {

        @NotBlank
        @Size(max = 100)
        private String name;

        @NotBlank
        @Email
        private String email;
    }

    @Data
    @NoArgsConstructor
    public static class MonetaryAmount {

        @NotNull
        @Min(0)
        private BigDecimal number;

        @NotNull
        private CurrencyUnit currency;
    }
}
