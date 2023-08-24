package com.tuum.cbs.models;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance {

    @Generated
    private UUID balanceId = UUID.randomUUID();
    private BigDecimal amount;
    private Currency currency;
}
