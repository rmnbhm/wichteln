package com.rmnbhm.wichteln.model;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;

public class MonetaryAmount {

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
