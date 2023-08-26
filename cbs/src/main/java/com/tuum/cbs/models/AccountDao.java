package com.tuum.cbs.models;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDao {
    private String country;
    private String customerId;
    private List<Currency> currencies;
}
