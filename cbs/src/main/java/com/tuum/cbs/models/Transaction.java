package com.tuum.cbs.models;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Transaction {
    private Long trxId;
    private UUID accountId;
    private BigDecimal amount;
    private BigDecimal balanceAfterTrx;
    private Currency currency;
    private TransactionType trxType;
    private String description;
}
