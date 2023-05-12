package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Currency;
import java.util.Objects;

public class Money extends ValueObject implements Comparable<Money>{
    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("BRL");
    private final BigDecimal amount;
    private final String currencyCode;

    public Money(BigDecimal value, Currency currency) {
        this(value, currency.getCurrencyCode());
    }

    private Money(BigDecimal amount, String currencyCode) {
        this.currencyCode = Objects.requireNonNull(currencyCode);
        this.amount = Objects.requireNonNull(amount).setScale(2, RoundingMode.HALF_EVEN);
    }

    public Money(BigDecimal value) {
        this(value, DEFAULT_CURRENCY);
    }

    public Money(double value, Currency currency) {
        this(new BigDecimal(value), currency.getCurrencyCode());
    }

    public Money(double value) {
        this(value, DEFAULT_CURRENCY);
    }

    private void assertSameCurrency(Money other) {
        if (!other.currencyCode.equals(this.currencyCode)) {
            // TODO change to a domain exception
            throw new IllegalArgumentException("Money objects must have the same currency");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.equals(money.amount) && currencyCode.equals(money.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currencyCode);
    }

    @Override
    public int compareTo(Money o) {
        assertSameCurrency(o);
        return Comparator.comparing((Money m) -> m.amount)
                .thenComparing(m -> m.currencyCode)
                .compare(this, o);
    }
}
