package com.tuum.cbs.models;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public class Balance {

    private Long balanceId;
    private BigDecimal amount;
    private Currency currency;
    private UUID accountId;

    public Balance(Long balanceId, BigDecimal amount, Currency currency, UUID accountId) {
        this.balanceId = balanceId;
        this.amount = amount;
        this.currency = currency;
        this.accountId = accountId;
    }

    public Balance() {
    }

    public Long getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "balanceId=" + balanceId +
                ", amount=" + amount +
                ", currency=" + currency +
                ", accountId=" + accountId +
                '}';
    }
}
