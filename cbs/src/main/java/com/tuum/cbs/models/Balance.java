package com.tuum.cbs.models;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance {

    private UUID balanceId;
    private BigDecimal amount;
    private Currency currency;
    private Long accountId;

//  public void setBalanceId() {
//        this.balanceId = UUID.randomUUID();
//    }
}
