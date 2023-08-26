package com.tuum.cbs.models;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransactionDao {
    private UUID accountId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType trxType;
    private String description;
}
