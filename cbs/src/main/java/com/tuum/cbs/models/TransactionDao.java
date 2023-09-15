package com.tuum.cbs.models;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public class TransactionDao {
    private UUID accountId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType trxType;
    private String description;

    public TransactionDao() {
    }

    public TransactionDao(UUID accountId, BigDecimal amount, Currency currency, TransactionType trxType, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.trxType = trxType;
        this.description = description;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
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

    public TransactionType getTrxType() {
        return trxType;
    }

    public void setTrxType(TransactionType trxType) {
        this.trxType = trxType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TransactionDao{" +
                "accountId=" + accountId +
                ", amount=" + amount +
                ", currency=" + currency +
                ", trxType=" + trxType +
                ", description='" + description + '\'' +
                '}';
    }
}
