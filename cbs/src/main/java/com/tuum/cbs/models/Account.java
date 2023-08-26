package com.tuum.cbs.models;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Account {

    private UUID accountId;
    private String customerId;
    private String country;
    private List<Balance> balanceList;

}
