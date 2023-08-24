package com.tuum.cbs.models;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    @NonNull
    private Long accountId;
    private Long customerId;
    private List<Balance> balanceList;

}
