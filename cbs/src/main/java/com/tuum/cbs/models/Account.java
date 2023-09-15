package com.tuum.cbs.models;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public class Account {

    private UUID accountId;
    private String customerId;
    private String country;
    private List<Balance> balanceList;

    public Account() {
    }

    public Account(UUID accountId, String customerId, String country, List<Balance> balanceList) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.country = country;
        this.balanceList = balanceList;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Balance> getBalanceList() {
        return balanceList;
    }

    public void setBalanceList(List<Balance> balanceList) {
        this.balanceList = balanceList;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", customerId='" + customerId + '\'' +
                ", country='" + country + '\'' +
                ", balanceList=" + balanceList +
                '}';
    }
}
