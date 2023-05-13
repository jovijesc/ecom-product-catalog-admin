package com.ecom.catalog.admin.domain.product;

import com.ecom.catalog.admin.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Currency;
import java.util.Objects;

public class Money extends ValueObject implements Comparable<Money> {
    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("BRL");
    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = Objects.requireNonNull(amount).setScale(2, RoundingMode.HALF_EVEN);
        this.currency = Objects.requireNonNull(currency);
    }

    public static Money from(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money with(BigDecimal amount) {
        return new Money(amount, DEFAULT_CURRENCY);
    }

    public static Money with(double amount) {
        return new Money(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    private void assertSameCurrency(Money other) {
        if (!other.currency.equals(this.currency)) {
            // TODO should use a domain exception like throw new IncompatibleCurrencyException("Money objects must have the same currency");
            throw new IllegalArgumentException("Money objects must have the same currency");
        }
    }

    public Money plus(Money other) {
        assertSameCurrency(other);
        BigDecimal result = this.amount.add(other.amount);
        return new Money(result, this.currency);
    }

    public Money minus(Money other) {
        assertSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        return new Money(result, this.currency);
    }

    public Money times(double multiplier) {
        BigDecimal result = this.amount.multiply(BigDecimal.valueOf(multiplier));
        return new Money(result, this.currency);
    }

    public Money dividedBy(double divisor) {
        BigDecimal result = this.amount.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_EVEN);
        return new Money(result, this.currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public int compareTo(Money o) {
        assertSameCurrency(o);
        return Comparator.comparing(Money::getAmount)
                .thenComparing((Money m) -> m.getCurrency().getCurrencyCode())
                .compare(this, o);
    }
}
