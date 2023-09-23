package com.tuum.cbs.common.util;

import com.tuum.cbs.models.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class TrxSignUtil {

    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "TrxSignUtil";

    public static BigDecimal addSignToAmount(BigDecimal amount, TransactionType trxType){
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " add sign to trx based on trxType");
        if (trxType.name().equalsIgnoreCase("IN")) {
            LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " added positive sign to trx as trxType: " + trxType);
            return amount;
        } else {
            LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " added negative sign to trx as trxType: " + trxType);
            return amount.negate();
        }
    }
}
