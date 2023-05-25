package com.ecom.catalog.admin.infrastructure.utils;

import org.zalando.jackson.datatype.money.MoneyModule;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public class MoneyUtils {

    private MoneyUtils(){}

    public static MonetaryAmount fromMoney(final com.ecom.catalog.admin.domain.product.Money aPrice) {
        return org.javamoney.moneta.Money.of(aPrice.getAmount(), aPrice.getCurrency().getCurrencyCode());
    }

    public static com.ecom.catalog.admin.domain.product.Money fromMonetaryAmount(final MonetaryAmount aPrice) {
        return com.ecom.catalog.admin.domain.product.Money.from(
                aPrice.getNumber().numberValue(BigDecimal.class),
                aPrice.getCurrency().getCurrencyCode()
        );
    }
}
