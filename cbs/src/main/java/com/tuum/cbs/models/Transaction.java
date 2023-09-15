package com.tuum.cbs.models;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public class Transaction {
    private Long trxId;
    private UUID accountId;
    private BigDecimal amount;
    private BigDecimal balanceAfterTrx;
    private Currency currency;
    private TransactionType trxType;
    private String description;

    public Transaction() {
    }

    public Transaction(Long trxId, UUID accountId, BigDecimal amount, BigDecimal balanceAfterTrx, Currency currency, TransactionType trxType, String description) {
        this.trxId = trxId;
        this.accountId = accountId;
        this.amount = amount;
        this.balanceAfterTrx = balanceAfterTrx;
        this.currency = currency;
        this.trxType = trxType;
        this.description = description;
    }

    public Long getTrxId() {
        return trxId;
    }

    public void setTrxId(Long trxId) {
        this.trxId = trxId;
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

    public BigDecimal getBalanceAfterTrx() {
        return balanceAfterTrx;
    }

    public void setBalanceAfterTrx(BigDecimal balanceAfterTrx) {
        this.balanceAfterTrx = balanceAfterTrx;
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
        return "Transaction{" +
                "trxId=" + trxId +
                ", accountId=" + accountId +
                ", amount=" + amount +
                ", balanceAfterTrx=" + balanceAfterTrx +
                ", currency=" + currency +
                ", trxType=" + trxType +
                ", description='" + description + '\'' +
                '}';
    }
}
