package com.tuum.cbs.models;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance {

    private Long balanceId;
    private BigDecimal amount;
    private Currency currency;
}
