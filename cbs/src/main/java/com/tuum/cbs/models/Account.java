package com.tuum.cbs.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    private UUID accountId;
    private String customerId;
    private String country;
    private List<Balance> balanceList;

}
