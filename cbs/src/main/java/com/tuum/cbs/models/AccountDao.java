package com.tuum.cbs.models;

import lombok.Builder;

import java.util.List;


@Builder
public class AccountDao {
    private String country;
    private String customerId;
    private List<Currency> currencies;

    public AccountDao() {
    }

    public AccountDao(String country, String customerId, List<Currency> currencies) {
        this.country = country;
        this.customerId = customerId;
        this.currencies = currencies;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    @Override
    public String toString() {
        return "AccountDao{" +
                "country='" + country + '\'' +
                ", customerId='" + customerId + '\'' +
                ", currencies=" + currencies +
                '}';
    }
}
